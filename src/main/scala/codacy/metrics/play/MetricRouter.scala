package codacy.metrics.play

import play.api.mvc._

class MetricRouter(metricsController:MetricController) extends (RequestHeader => Option[Handler]) {
  override def apply(request: RequestHeader): Option[Handler] = Option(request.path).collect{
    case MetricConfiguration.metricsPath =>
      metricsController.metrics
    case MetricConfiguration.healthPath =>
      metricsController.health
  }
}
