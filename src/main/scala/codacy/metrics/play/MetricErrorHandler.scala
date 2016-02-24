package codacy.metrics.play

import codacy.metrics.dropwizard.mark
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc._
import play.api.routing.Router
import play.api.{Configuration, Environment}
import play.core.SourceMapper

import scala.concurrent.Future

//for compile time dependency injection
class MetricErrorHandler(environment: Environment,
                         configuration: Configuration,
                         sourceMapper: Option[SourceMapper] = None,
                         router: => Option[Router] = None)
  extends DefaultHttpErrorHandler(environment, configuration, sourceMapper, router) {

  final override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    if (!excludeRequest(request)) mark(configuration.failedRequests)
    onClientErrorImpl(request, statusCode, message)
  }

  def onClientErrorImpl(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    super.onClientError(request, statusCode)
  }

  final override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    if (!excludeRequest(request)) mark(configuration.failedRequests)
    onServerErrorImpl(request, exception)
  }

  def onServerErrorImpl(request: RequestHeader, exception: Throwable): Future[Result] = {
    super.onServerError(request, exception)
  }

}
