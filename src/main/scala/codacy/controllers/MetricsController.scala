package codacy.controllers

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.health.HealthCheckRegistry
import play.api.libs.json.{OWrites, Json}
import play.api.mvc.{Action, Controller}
import scala.collection.JavaConversions._

trait MetricsController{ self: Controller =>

  def metricRegistry:MetricRegistry
  def healthCheckRegistry:HealthCheckRegistry

  def excludedHealthChecks:Set[String]

  def health = Action {

    val includedChecks = healthCheckRegistry.getNames.--(excludedHealthChecks).
      map(healthCheckRegistry.runHealthCheck)

    if(includedChecks.forall(_.isHealthy)) Ok
    else InternalServerError
  }

  def metrics = Action{
    val healthJson = Json.obj(
      "healthChecks" -> implicitly[OWrites[HealthCheckResult]].writes(healthCheckRegistry.runHealthChecks())
    )

    Ok(
      implicitly[OWrites[MetricRegistry]].writes(metricRegistry) ++ healthJson
    )
  }
}