package codacy.metrics.play

import codacy.metrics.dropwizard._
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.{Action, Controller}

trait MetricController{ self: Controller =>

  def excludedHealthChecks:Set[HealthCheckName]

  def health = Action {

    val includedChecks = ( HealthCheckRegistry.names -- excludedHealthChecks ).
      map(HealthCheckRegistry.runHealthCheck)

    if(includedChecks.forall(_.isHealthy)) Ok
    else InternalServerError
  }

  def metrics = Action{
    val healthCheckResults:HealthCheckResults = HealthCheckRegistry.runHealthChecks()

    val healthJson = Json.obj(
      "healthChecks" -> implicitly[OWrites[HealthCheckResults]].writes(healthCheckResults)
    )

    Ok(
      implicitly[OWrites[MetricRegistry]].writes(MetricRegistry) ++ healthJson
    )
  }
}