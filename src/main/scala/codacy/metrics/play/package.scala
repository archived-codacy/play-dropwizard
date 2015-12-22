package codacy.metrics


import _root_.play.api.mvc.RequestHeader
import codacy.filters.{RequestsTime, FailedRequests, SuccessfulRequests}
import codacy.json.DropwizardJson
import codacy.metrics.play.MetricConfiguration._

package object play extends DropwizardJson{

  val databasePrefix = "database:"

  private[play] def excludeRequest(request:RequestHeader) = Set(metricsPath,healthPath).contains(request.path)

  private[play] val metricFilters = Seq(
    SuccessfulRequests(successfulRequests,excludeRequest),
    FailedRequests(failedRequests,excludeRequest),
    RequestsTime(excludeRequest)
  )
}
