package codacy.metrics.play

import codacy.metrics.dropwizard._
import play.api.Play

object MetricConfiguration{

  private[this] def config = Play.current.configuration

  lazy val metricsPath = config.getString("metric.paths.metrics").getOrElse("/metrics")
  lazy val healthPath  = config.getString("metric.paths.health" ).getOrElse("/health")

  lazy val requests           = MeterName(config.getString("metric.meters.requests").getOrElse("requests"))
  lazy val successfulRequests = MeterName(config.getString("metric.meters.successfulRequests").getOrElse("successfulRequests"))
  lazy val failedRequests     = MeterName(config.getString("metric.meters.failedRequests").getOrElse("failedRequests"))
}