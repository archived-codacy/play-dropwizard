package codacy

import codacy.controllers.MetricsController
import codacy.filters.{FailedRequests, SuccessfulRequests}
import codacy.plugin.Metrics
import com.codahale.metrics.{Meter, MetricRegistry}
import com.codahale.metrics.health.HealthCheckRegistry
import play.api.{Play, Configuration}
import play.api.mvc._
import scala.collection.JavaConversions._
import scala.concurrent.Future
import play.api.Play.current

object MetricDefaults{

  val metricRegistry = Metrics.metricRegistry
  val healthCheckRegistry = Metrics.healthCheckRegistry
  val config = Play.current.configuration

  lazy val metricsPath = config.getString("metric.paths.metrics").getOrElse("/metrics")
  lazy val healthPath  = config.getString("metric.paths.health").getOrElse("/health")

  def excludeRequest(r:RequestHeader) = Set(metricsPath,healthPath).contains(r.path)

  val successfulRequestMeter = metricRegistry.meter("successfulRequests")
  val failedRequestMeter     = metricRegistry.meter("failedRequests")
  val requestMeter           = metricRegistry.meter("requests")

  val metricFilters = Seq(
    new SuccessfulRequests(successfulRequestMeter,excludeRequest = excludeRequest),
    new FailedRequests(failedRequestMeter,excludeRequest = excludeRequest)
  )
}

trait DefaultMetricController extends Controller with MetricsController{

  override def metricRegistry: MetricRegistry = MetricDefaults.metricRegistry
  override def excludedHealthChecks: Set[String] = Set.empty ++ healthCheckRegistry.getNames.filterNot(_.startsWith(plugin.databasePrefix))
  override def healthCheckRegistry: HealthCheckRegistry = MetricDefaults.healthCheckRegistry
}

object DefaultMetricController extends DefaultMetricController

class DefaultMetricGlobal(filters: EssentialFilter*) extends WithFilters(filters ++ MetricDefaults.metricFilters:_*){ self =>
  import MetricDefaults._

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
