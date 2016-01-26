package codacy.metrics.cachet

import play.api.{Configuration, Play}

trait CachetConfiguration{

  def underlying: Configuration

  object cachet{
    lazy val token   = underlying.getString("codacy.metric.cachet.token").filter(_.nonEmpty)
    lazy val url     = underlying.getString("codacy.metric.cachet.url").filter(_.nonEmpty)
    lazy val metrics = underlying.getBoolean("codacy.metric.cachet.metricsEnabled").getOrElse(false)
    lazy val enabled = underlying.getBoolean("codacy.metric.cachet.isEnabled").getOrElse(false)
  }
}

object CachetConfiguration extends CachetConfiguration {
  override def underlying: Configuration = Play.current.configuration
}