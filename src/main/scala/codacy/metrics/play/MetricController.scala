package codacy.metrics.play

import java.util.concurrent.Executors

import codacy.metrics.dropwizard._
import com.codahale.metrics.health.HealthCheck
import play.api.libs.concurrent.Promise
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.{Action, Controller}
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContextExecutor, ExecutionContext, Future}

private[play] object DropwizardExecutionContext{

  object Implicits{
    implicit val default: ExecutionContextExecutor = default
  }

  lazy val default: ExecutionContextExecutor =  ExecutionContext.fromExecutor( Executors.newCachedThreadPool() )
}

trait MetricController{ self: Controller =>
  import DropwizardExecutionContext.Implicits._

  def isExcludedHealthCheck: HealthCheckName => Boolean

  def health = Action.async{ req =>

    val includedChecks = ( HealthCheckRegistry.names.filterNot(isExcludedHealthCheck) ).map( HealthCheckRegistry.runHealthCheckAsync(_) )

    Future.sequence(includedChecks).map{ case checks =>
      if(checks.forall(_.isHealthy)) Ok
      else InternalServerError
    }
  }

  def metrics = Action.async{ req =>

    HealthCheckRegistry.runHealthChecksAsync().map{ case healthCheckResults:HealthCheckResults =>

      val healthJson = Json.obj(
        "healthChecks" -> implicitly[OWrites[HealthCheckResults]].writes(healthCheckResults)
      )

      Ok(
        implicitly[OWrites[MetricRegistry]].writes(MetricRegistry) ++ healthJson
      )
    }
  }
}