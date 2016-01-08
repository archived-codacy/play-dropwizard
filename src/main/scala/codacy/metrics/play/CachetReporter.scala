package codacy.metrics.play

import java.util
import java.util.concurrent.TimeUnit
import codacy.metrics.cachet._
import codacy.metrics.dropwizard._
import com.codahale.metrics.{Meter, Counter, MetricFilter, Gauge, Histogram, ScheduledReporter}
import org.joda.time.DateTime
import play.api.Application
import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Await, Future}
import scala.concurrent.duration._
import scala.util.{Success, Failure, Try}

private[play] case class CachetReporter(component:ResponseComponent, registry: MetricRegistry=MetricRegistry, name: String="CachetReporter",
                                        filter: MetricFilter=MetricFilter.ALL, rateUnit: TimeUnit=TimeUnit.SECONDS,
                                        durationUnit: TimeUnit=TimeUnit.SECONDS)(implicit app:Application, executionContext: ExecutionContext)
  extends ScheduledReporter(registry,name,filter,rateUnit,durationUnit){


  private[this] def metricName(name:String) = MetricName(s"${component.name}:$name")

  override def report(gauges: util.SortedMap[String, Gauge[_]], counters: util.SortedMap[String, Counter],
                      histograms: util.SortedMap[String, Histogram], meters: util.SortedMap[String, Meter],
                      timers: util.SortedMap[String, Timer]): Unit = {
    val date = DateTime.now()

    Cachet.metrics.list().map{ case metrics: Set[ResponseMetric] =>
      lazy val mapping = metrics.map{ case m => (m.name,m.id) }.toMap

      val counterPs = counters.map{ case (n,counter) =>
        val name = metricName(n)
        lazy val point = CreatePoint(PointValue(counter.getCount),Some(date))

        mapping.get(name).map( Future.successful ).getOrElse(create(name,counter)).flatMap{ case metricId =>
          Cachet.metrics.points(metricId).create(point)
        }
      }

      val counterPoints = Future.sequence(counterPs)

      val timerPs = timers.map{ case (n,timer) =>
        val name = metricName(n)
        lazy val point = CreatePoint(PointValue(math.round(timer.getOneMinuteRate)),Some(date))

        mapping.get(name).map( Future.successful ).getOrElse(create(name,timer)).flatMap{ case metricId =>
          Cachet.metrics.points(metricId).create(point)
        }
      }

      val timerPoints = Future.sequence(timerPs)

      val meterPs = meters.map{ case (n,meter) =>
        val name = metricName(n)
        lazy val point = CreatePoint(PointValue(meter.getCount),Some(date))

        mapping.get(name).map( Future.successful ).getOrElse(create(name,meter)).flatMap{ case metricId =>
          Cachet.metrics.points(metricId).create(point)
        }
      }

      val meterPoints = Future.sequence(meterPs)

      Try(
        Await.result(Future.sequence(Seq(counterPoints,timerPoints,meterPoints)), 1.minute)
      )
    }
  }

  private[this] def create(name:MetricName,counter:Counter):Future[MetricId] = {
    val obj = CreateMetric(name,MetricSuffix("TODOsuffix"),MetricDescription("counter"),MetricDefaultValue(0),true)
    Cachet.metrics.create(obj).map(_.id)
  }

  private[this] def create(name:MetricName,timer:Timer):Future[MetricId] = {
    val obj = CreateMetric(name,MetricSuffix("TODOsuffix"),MetricDescription("timer"),MetricDefaultValue(0),true)
    Cachet.metrics.create(obj).map(_.id)
  }

  private[this] def create(name:MetricName,timer:Meter):Future[MetricId] = {
    val obj = CreateMetric(name,MetricSuffix("TODOsuffix"),MetricDescription("meter"),MetricDefaultValue(0),true)
    Cachet.metrics.create(obj).map(_.id)
  }
}
