package codacy.metrics.play

import codacy.metrics.dropwizard._
import play.api.mvc._

import scala.concurrent.Future

class MetricGlobal(filters: EssentialFilter*) extends WithFilters(filters ++ metricFilters:_*){ self =>
  import MetricConfiguration._

  private[this] val metricsRouter = new MetricRouter(DefaultMetricController)

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    //if(! excludeRequest(request)) Metrics.mark(requestsName)
    metricsRouter(request).orElse(super[WithFilters].onRouteRequest(request))
  }

  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = {
    if(! excludeRequest(request)) failedRequests.mark
    super[WithFilters].onError(request,ex)
  }

  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
    if(! excludeRequest(request)) failedRequests.mark
    super[WithFilters].onBadRequest(request,error)
  }
}
