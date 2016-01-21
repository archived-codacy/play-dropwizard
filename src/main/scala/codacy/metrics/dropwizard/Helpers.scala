package codacy.metrics.dropwizard

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

private[dropwizard] trait Helpers{

  def timed[A](timerName: TimerName)(block: => A): A = {
    val ctx = MetricRegistry.timer(timerName).map(_.time())
    val res = Try(block)
    ctx.foreach(_.stop())
    res match{
      case Success(result) => result
      case Failure(exception) => throw exception
    }
  }

  def timedAsync[A](timerName: TimerName)(block: => Future[A]): Future[A] = {
    val ctx = MetricRegistry.timer(timerName).map(_.time())
    val res = block
    res.onComplete(_ => ctx.foreach(_.stop()))(DropwizardExecutionContext.default)
    res
  }
}
