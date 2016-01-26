package codacy.metrics.play

import codacy.metrics.dropwizard._
import play.api.{Logger, Configuration, Play}

trait MetricConfiguration{

  def underlying: Configuration

  lazy val metricsPath      = underlying.getString("codacy.metric.paths.metrics").getOrElse("/metrics")
  lazy val healthPath       = underlying.getString("codacy.metric.paths.health" ).getOrElse("/health")
  lazy val databaseMetrics  = underlying.getBoolean("codacy.metric.dbMetricsEnabled").getOrElse(false)

  object graphite{
    lazy val isEnabled = underlying.getBoolean("codacy.metric.graphite.isEnabled").getOrElse(false)
    lazy val hostname  = withDeprecatedString("codacy.metric.graphite.hostname","codacy.metric.graphiteHostname")
    lazy val port      = withDeprecatedInt("codacy.metric.graphite.port","codacy.metric.graphitePort").getOrElse(2003)
    lazy val prefix    = withDeprecatedString("codacy.metric.graphite.prefix","codacy.metric.graphitePrefix")
  }

  lazy val requests           = MeterName(/*config.getString("metric.meters.requests").getOrElse(*/"requests")//)
  lazy val successfulRequests = MeterName(/*config.getString("metric.meters.successfulRequests").getOrElse(*/"successfulRequests")//)
  lazy val failedRequests     = MeterName(/*config.getString("metric.meters.failedRequests").getOrElse(*/"failedRequests")//)

  private[this] def withDeprecatedString(key:String, fallbackKey:String, acceptEmpty:Boolean=false) = {
    val value = underlying.getString(key).orElse(
      underlying.getString(fallbackKey).map{ case oldKey =>
        Logger.warn(s"key: $fallbackKey is deprecated, use $key instead")
        oldKey
      }
    )
    if (acceptEmpty) value else value.filter(_.nonEmpty)
  }

  private[this] def withDeprecatedInt(key:String, fallbackKey:String) = {
    underlying.getInt(key).orElse(
      underlying.getInt(fallbackKey).map{ case oldKey =>
        Logger.warn(s"key: $fallbackKey is deprecated, use $key instead")
        oldKey
      }
    )
  }
}

object MetricConfiguration extends MetricConfiguration {
  override def underlying: Configuration = Play.current.configuration
}