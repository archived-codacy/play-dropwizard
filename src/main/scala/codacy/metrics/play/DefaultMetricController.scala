package codacy.metrics.play

import akka.actor.ActorSystem
import play.api.libs.concurrent.Akka
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext

class DefaultMetricController(val actorSystem: ActorSystem )(implicit val executionContext: ExecutionContext) extends Controller with MetricController{
  override def isExcludedHealthCheck = isDatabaseHealthCheck
}

object DefaultMetricController extends DefaultMetricController({
  import play.api.Play.current
  Akka.system
})(play.api.libs.concurrent.Execution.defaultContext)
