package codacy.metrics

import _root_.play.api.Configuration
import _root_.play.api.mvc.{Filter, RequestHeader}
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

  lazy val successfulRequestsFilter = SuccessfulRequests(successfulRequests,excludeRequest)
  lazy val failedRequestsFilter     = FailedRequests(failedRequests,excludeRequest)
  lazy val requestTime              = RequestsTime(excludeRequest)
  lazy val requestStatus            = RequestStatus(excludeRequest)

  lazy val metricFilters: Seq[Filter] = Seq(
    successfulRequestsFilter,
    failedRequestsFilter,
    requestTime,
    requestStatus
  )

  implicit class ConfigurationExtension(val underlying: Configuration) extends MetricConfiguration
}
