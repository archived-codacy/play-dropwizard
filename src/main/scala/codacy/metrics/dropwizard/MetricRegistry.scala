package codacy.metrics.dropwizard

object MetricRegistry{
  private[dropwizard] val self = new com.codahale.metrics.MetricRegistry

  def timer(timerName: TimerName):Timer = self.timer(timerName.value)
}
