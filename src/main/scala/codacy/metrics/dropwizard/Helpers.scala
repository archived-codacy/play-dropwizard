package codacy.metrics.dropwizard

import scala.concurrent.Future

private[dropwizard] trait Helpers{

  def timed[A](timerName: TimerName)(block: => A): A = {
    val ctx = MetricRegistry.timer(timerName).time()
    val res = block
    ctx.stop()
    res
  }

  def timedAsync[A](timerName: TimerName)(block: => Future[A]): Future[A] = {
    val ctx = MetricRegistry.timer(timerName).time()
    val res = block
    res.onComplete(_ => ctx.stop())(DropwizardExecutionContext.default)
    res
  }
}
