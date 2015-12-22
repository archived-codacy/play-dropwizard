package codacy.metrics.play

import codacy.metrics.dropwizard._
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.{Action, Controller}

import scala.collection.JavaConversions._

trait MetricController{ self: Controller =>

  def excludedHealthChecks:Set[String]

  def health = Action {

    val includedChecks = HealthCheckRegistry.getNames.--(excludedHealthChecks).
      map(HealthCheckRegistry.runHealthCheck)

    if(includedChecks.forall(_.isHealthy)) Ok
    else InternalServerError
  }

  def metrics = Action{
    val healthJson = Json.obj(
      "healthChecks" -> implicitly[OWrites[HealthCheckResult]].writes(HealthCheckRegistry.runHealthChecks())
    )

    Ok(
      implicitly[OWrites[MetricRegistry]].writes(MetricRegistry) ++ healthJson
    )
  }
}