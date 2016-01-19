package codacy.metrics.play

import codacy.metrics.dropwizard._
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}

trait MetricController{ self: Controller =>

  implicit def executionContext:ExecutionContext

  implicit def actorSystem: akka.actor.ActorSystem

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