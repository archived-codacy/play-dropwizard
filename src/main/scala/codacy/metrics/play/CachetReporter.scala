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
import scala.util.Try

private[play] case class CachetReporter(registry: MetricRegistry=MetricRegistry, name: String="CachetReporter",
                                        filter: MetricFilter=MetricFilter.ALL, rateUnit: TimeUnit=TimeUnit.SECONDS,
                                        durationUnit: TimeUnit=TimeUnit.SECONDS)(implicit app:Application, executionContext: ExecutionContext)
  extends ScheduledReporter(registry,name,filter,rateUnit,durationUnit){

  override def report(gauges: util.SortedMap[String, Gauge[_]], counters: util.SortedMap[String, Counter],
                      histograms: util.SortedMap[String, Histogram], meters: util.SortedMap[String, Meter],
                      timers: util.SortedMap[String, Timer]): Unit = {
    val date = Some((DateTime.now().getMillis / 1000).toString)

    Cachet.metrics.list().map{ case metrics =>
      val tt = counters.toList
      val ttr = timers.toList

      val mapping = metrics.map{ case m => (m.name,m.id) }.toMap

      val counterPoints = Future.sequence(counters.map{ case (n,counter) =>
        val name = MetricName(n)
        lazy val point = CreatePoint(PointValue(counter.getCount),date)

        mapping.get(name).map( Future.successful ).getOrElse(create(name,counter)).flatMap{ case metricId =>
          Cachet.metrics.points(metricId).create(point)
        }
      })

      val timerPs = ttr.map{ case (n,timer) =>
        val name = MetricName(n)
        lazy val point = CreatePoint(PointValue(math.round(timer.getOneMinuteRate)),date)

        mapping.get(name).map( Future.successful ).getOrElse(create(name,timer)).flatMap{ case metricId =>
          Cachet.metrics.points(metricId).create(point)
        }
      }

      val timerPoints = Future.sequence(timerPs)

      Try(
        Await.result(Future.sequence(Seq(counterPoints,timerPoints)), 1.minute)
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

}
