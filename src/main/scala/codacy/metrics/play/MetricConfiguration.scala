package codacy.metrics.play

import codacy.metrics.dropwizard._
import play.api.{Configuration, Play}

trait MetricConfiguration{

  def underlying: Configuration

  lazy val metricsPath     = underlying.getString("codacy.metric.paths.metrics").getOrElse("/metrics")
  lazy val healthPath      = underlying.getString("codacy.metric.paths.health" ).getOrElse("/health")
  lazy val databaseMetrics = underlying.getBoolean("codacy.metric.dbMetricsEnabled").getOrElse(false)

  lazy val requests           = MeterName(/*config.getString("metric.meters.requests").getOrElse(*/"requests")//)
  lazy val successfulRequests = MeterName(/*config.getString("metric.meters.successfulRequests").getOrElse(*/"successfulRequests")//)
  lazy val failedRequests     = MeterName(/*config.getString("metric.meters.failedRequests").getOrElse(*/"failedRequests")//)
}

object MetricConfiguration extends MetricConfiguration {
  override def underlying: Configuration = Play.current.configuration
}