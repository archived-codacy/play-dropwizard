package codacy.metrics.cachet

import play.api.{Configuration, Play}

trait CachetConfiguration {

  def underlying: Configuration

  def cachetToken   = underlying.getString("codacy.metric.cachet.token")
  def cachetUrl     = underlying.getString("codacy.metric.cachet.url")
  def cachetMetrics = underlying.getBoolean("codacy.metric.cachet.metricsEnabled").getOrElse(false)
  def cachetEnabled = cachetToken.isDefined && cachetUrl.isDefined
}

object CachetConfiguration extends CachetConfiguration {
  override def underlying: Configuration = Play.current.configuration
}