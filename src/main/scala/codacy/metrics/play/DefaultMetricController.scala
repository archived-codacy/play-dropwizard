package codacy.metrics.play

import codacy.metrics.dropwizard._
import play.api.mvc.Controller

trait DefaultMetricController extends Controller with MetricController{
  override def excludedHealthChecks: Set[HealthCheckName] = Set.empty ++ HealthCheckRegistry.names.filterNot( isDatabaseHealthCheck )
}

object DefaultMetricController extends DefaultMetricController
