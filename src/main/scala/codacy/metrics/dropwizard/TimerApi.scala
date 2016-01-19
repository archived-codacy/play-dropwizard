package codacy.metrics.dropwizard

import java.util.concurrent.TimeUnit

import org.joda.time.{Duration, DateTime}

private[dropwizard] trait TimerApi {


  implicit class TimerExtension(timer:Timer){

    def time(start:DateTime,end:DateTime):Timer = {
      val duration = new Duration(start,end)
      timer.update(duration.getMillis, TimeUnit.MILLISECONDS)
      timer
    }
  }
}
