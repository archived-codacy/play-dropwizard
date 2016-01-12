package codacy.metrics

import _root_.play.api.Configuration

package object cachet extends Formats with WsApi with Crud with Cruds{

  implicit class ConfigurationExtension(val underlying:Configuration) extends AnyVal with CachetConfiguration
}
