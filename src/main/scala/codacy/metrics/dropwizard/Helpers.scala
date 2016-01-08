package codacy.metrics.dropwizard

private[dropwizard] trait Helpers{

  def timed[A](timerName: TimerName)(block: => A) = {
    import codacy.metrics.dropwizard._
    val ctx = MetricRegistry.timer(timerName).time()
    val res = block
    ctx.stop()
    res
  }

}
