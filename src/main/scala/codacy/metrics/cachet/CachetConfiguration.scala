package codacy.metrics.cachet

import play.api.Play

object CachetConfiguration {

  private[this] def config = Play.current.configuration

  lazy val cachetToken   = config.getString("codacy.metric.cachet.token")
  lazy val cachetUrl     = config.getString("codacy.metric.cachet.url")
  lazy val cachetEnabled = cachetToken.isDefined && cachetUrl.isDefined
}
