package codacy.metrics


import _root_.play.api.mvc.RequestHeader
import codacy.filters.{RequestsTime, FailedRequests, SuccessfulRequests}
import codacy.json.DropwizardJson
import codacy.metrics.dropwizard.HealthCheckName
import codacy.metrics.play.MetricConfiguration._

package object play extends DropwizardJson{

  private[play] val databasePrefix = "database:"

  def isDatabaseHealthCheck(healthCheckName: HealthCheckName) = {
    healthCheckName.value.startsWith(databasePrefix)
  }

  private[play] def excludeRequest(request:RequestHeader) = Set(metricsPath,healthPath).contains(request.path)

  private[play] val metricFilters = Seq(
    SuccessfulRequests(successfulRequests,excludeRequest),
    FailedRequests(failedRequests,excludeRequest),
    RequestsTime(excludeRequest)
  )
}
