package codacy

import codacy.controllers.MetricsController
import codacy.filters.{FailedRequests, SuccessfulRequests}
import codacy.plugin.{MetricPlugin, Metrics}
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.health.HealthCheckRegistry
import play.api.Play
import play.api.mvc._
import scala.collection.JavaConversions._
import scala.concurrent.Future

object MetricDefaults{

  private[this] def config = Play.current.configuration

  def metricsPath = config.getString("metric.paths.metrics").getOrElse("/metrics")
  def healthPath  = config.getString("metric.paths.health" ).getOrElse("/health")

  def requestsName = config.getString("metric.meters.requests").getOrElse("requests")
  def successfulRequestsName = config.getString("metric.meters.successfulRequests").getOrElse("successfulRequests")
  def failedRequestsName  = config.getString("metric.meters.failedRequests").getOrElse("failedRequests")

  def excludeRequest(r:RequestHeader) = Set(metricsPath,healthPath).contains(r.path)

  lazy val metricFilters = Seq(
    new SuccessfulRequests(successfulRequestsName,excludeRequest = excludeRequest),
    new FailedRequests(failedRequestsName,excludeRequest = excludeRequest)
  )
}

trait DefaultMetricController extends Controller with MetricsController{
  import play.api.Play.current
  override def metricRegistry: MetricRegistry = Metrics.metricRegistry
  override def excludedHealthChecks: Set[String] = Set.empty ++ healthCheckRegistry.getNames.filterNot(_.startsWith(plugin.databasePrefix))
  override def healthCheckRegistry: HealthCheckRegistry = Metrics.healthCheckRegistry
}

object DefaultMetricController extends DefaultMetricController

class DefaultMetricGlobal(filters: EssentialFilter*) extends WithFilters(filters ++ MetricDefaults.metricFilters:_*){ self =>
  import MetricDefaults._
  import play.api.Play.current
  def metricRegistry: MetricRegistry = Metrics.metricRegistry
  def healthCheckRegistry = Metrics.healthCheckRegistry

  def successfulRequestMeter = metricRegistry.meter(successfulRequestsName)
  def failedRequestMeter     = metricRegistry.meter(failedRequestsName)
  def requestMeter           = metricRegistry.meter(requestsName)

  def metricsController:MetricsController = DefaultMetricController

  def onMetricsRouteRequest:PartialFunction[RequestHeader,Handler] = {
    case request if request.path == metricsPath =>
      metricsController.metrics
    case request if request.path == healthPath =>
      metricsController.health
  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {

    if(! excludeRequest(request)) requestMeter.mark()
    onMetricsRouteRequest.lift.apply(request).orElse(super[WithFilters].onRouteRequest(request))
  }

  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = {
    if(! excludeRequest(request)) failedRequestMeter.mark()
    super[WithFilters].onError(request,ex)
  }

  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
    if(! excludeRequest(request)) failedRequestMeter.mark()
    super[WithFilters].onBadRequest(request,error)
  }
}
