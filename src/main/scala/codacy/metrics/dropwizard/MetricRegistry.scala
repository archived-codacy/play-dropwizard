package codacy.metrics.dropwizard
import com.codahale.metrics.{MetricRegistry => DropwizardMetricRegistry, Histogram, Meter}

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
