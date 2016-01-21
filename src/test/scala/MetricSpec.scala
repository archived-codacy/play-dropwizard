package codacy.metrics.dropwizard

import org.joda.time.{Duration, DateTime}
import org.specs2.mutable.Specification
import scala.collection.JavaConversions._

class ThirdPartySpecs extends Specification{

  def allTimers = MetricRegistry.underlying.getTimers

  "A timer" should{

    val timerName = TimerName("myTimer")

    "be created" in{

      timed(timerName)( "" )

      val timer = allTimers.find{ case (name,_) => name == timerName.value }

      timer must beSome
    }

    "be prefixed correctly" in {
      timed(timerName)( "" )

      val registeredName = allTimers.collectFirst{ case (name,_) if name == timerName.value => name}

      registeredName must beSome.which( _.startsWith("timer."))
    }

    "have exactly one timing instance" in {
      val timerName = TimerName("newTimer")
      timed(timerName)( "" )

      val firstTimeMeasurement =
        allTimers.collectFirst{ case (name,timer) if name == timerName.value =>
          timer.getSnapshot.getValues.size
        }

      firstTimeMeasurement must beSome.which( _ == 1)
    }

    "measure time" in {
      timed(timerName)( Thread.sleep(500) )

      val firstTimeMeasurement =
        allTimers.collectFirst{ case (name,timer) if name == timerName.value =>
          timer.getSnapshot.getValues.head
        }

      firstTimeMeasurement must beSome.which( _ > 0)
    }

    "should register a manual measurement" in{
      val start = DateTime.now()
      val end = start.plusMinutes(2)
      lazy val nanos = new Duration(start,end).getMillis * 1000000
      time(timerName,start,end)

      val lastMeasurement = allTimers.collectFirst{ case (name,timer) if name == timerName.value =>
        timer.getSnapshot.getValues.last
      }

      lastMeasurement must beSome.which( _ == nanos)
    }
  }
}
