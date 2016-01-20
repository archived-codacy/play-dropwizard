package codacy.metrics.dropwizard

import java.util.concurrent.TimeUnit

import com.codahale.metrics.{MetricRegistry => DropwizardMetricRegistry, Histogram, Meter}
import org.joda.time.{Duration, DateTime}

import scala.util.Try

trait MetricRegistry extends MetricRegistryApi{
  private[dropwizard] def underlying:DropwizardMetricRegistry
}

object MetricRegistry extends MetricRegistry{
  private[dropwizard] lazy val underlying:DropwizardMetricRegistry = new DropwizardMetricRegistry
}

private[dropwizard] trait MetricRegistryApi{
  import MetricRegistry.underlying

  def mark(meterName: MeterName, n:Long=1): Unit =
    meter(meterName).foreach(_.mark(n))

  def update(histogramName: HistogramName, value:Long): Unit =
    histogram(histogramName).foreach(_.update(value))

  def time(timerName: TimerName, start:DateTime,end:DateTime):Unit = {
    val duration = new Duration(start,end)
    timer(timerName).foreach(_.update(duration.getMillis, TimeUnit.MILLISECONDS))
  }

  private[metrics] def meter(meterName: MeterName): Try[Meter] = Try(
    underlying.meter(meterName.value)
  )

  private[metrics] def timer(timerName: TimerName) = Try(
    underlying.timer(timerName.value)
  )

  private[metrics] def histogram(histogramName: HistogramName) = Try(
    underlying.histogram(histogramName.value)
  )
}
