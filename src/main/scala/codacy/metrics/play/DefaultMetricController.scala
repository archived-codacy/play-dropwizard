package codacy.metrics.play

import play.api.mvc.Controller

trait DefaultMetricController extends Controller with MetricController{
  override def isExcludedHealthCheck = isDatabaseHealthCheck
}

object DefaultMetricController extends DefaultMetricController
