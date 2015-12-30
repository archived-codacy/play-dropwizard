package codacy.metrics.dropwizard
import com.codahale.metrics.{MetricRegistry => DropwizardMetricRegistry, Meter}

trait MetricRegistry extends MetricRegistryApi{
  private[dropwizard] def underlying:DropwizardMetricRegistry
}

object MetricRegistry extends MetricRegistry{
  private[dropwizard] val underlying:DropwizardMetricRegistry = new DropwizardMetricRegistry
}

private[dropwizard] trait MetricRegistryApi{
  import MetricRegistry.underlying

  def meter(meterName: MeterName): Meter =
    underlying.meter(meterName.value)

  def timer(timerName: TimerName):Timer =
    underlying.timer(timerName.value)

  def mark(meterName: MeterName, n:Long=1): Unit =
    meter(meterName).mark(n)
}
