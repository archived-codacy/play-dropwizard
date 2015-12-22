package codacy.metrics.play

import codacy.metrics.dropwizard._
import play.api.mvc.Controller

import scala.collection.JavaConversions._

trait DefaultMetricController extends Controller with MetricController{
  override def excludedHealthChecks: Set[String] = Set.empty ++ HealthCheckRegistry.getNames.filterNot(_.startsWith(databasePrefix))
}

object DefaultMetricController extends DefaultMetricController
