package codacy.metrics.dropwizard

private[dropwizard] trait Helpers{

  def timed[A](timerName: TimerName)(block: => A) = {
    val ctx = MetricRegistry.timer(timerName).time()
    val res = block
    ctx.stop()
    res
  }

}
