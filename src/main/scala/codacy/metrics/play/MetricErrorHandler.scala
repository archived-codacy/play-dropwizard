package codacy.metrics.play

import play.api.http.{HttpErrorHandler, DefaultHttpErrorHandler}
import play.api.routing.Router
import play.api.{Configuration, Environment}
import play.core.SourceMapper

import scala.concurrent.Future

//for compile time dependency injection
final class MetricErrorHandler(environment: Environment,
                               configuration: Configuration,
                               sourceMapper: Option[SourceMapper] = None,
                               router: => Option[Router] = None,
                               errorHandler: => Option[HttpErrorHandler]=None) extends HttpErrorHandler{
  import play.api.mvc._
  import codacy.metrics.dropwizard.{Result => _, _}

  private[this] lazy val baseErrorHandler:HttpErrorHandler = {
    errorHandler.getOrElse(new DefaultHttpErrorHandler(environment,configuration,sourceMapper,router))
  }

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    if(! excludeRequest(request)) mark(configuration.failedRequests)
    baseErrorHandler.onClientError(request,statusCode)
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    if(! excludeRequest(request)) mark(configuration.failedRequests)
    baseErrorHandler.onServerError(request,exception)
  }
}

