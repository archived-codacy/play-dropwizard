package codacy.metrics

import _root_.play.api.Configuration
import _root_.play.api.mvc.RequestHeader
import codacy.metrics.json.DropwizardJson
import codacy.metrics.dropwizard.HealthCheckName
import codacy.metrics.play.MetricConfiguration._
import codacy.metrics.play.filters._

package object play extends DropwizardJson{

  private[play] val databasePrefix = "database:"

  def isDatabaseHealthCheck(healthCheckName: HealthCheckName) = {
    healthCheckName.value.startsWith(databasePrefix) || healthCheckName.value.startsWith("HikariPool-")
  }

  def excludeRequest(request:RequestHeader) = Set(metricsPath,healthPath).contains(request.path)

  lazy val metricFilters = Seq(
    SuccessfulRequests(successfulRequests,excludeRequest),
    FailedRequests(failedRequests,excludeRequest),
    RequestsTime(excludeRequest),
    RequestStatus(excludeRequest)
  )

  implicit class ConfigurationExtension(val underlying: Configuration) extends MetricConfiguration
}
