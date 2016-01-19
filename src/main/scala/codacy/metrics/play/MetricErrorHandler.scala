package codacy.metrics.play

import play.api.http.DefaultHttpErrorHandler
import play.api.routing.Router
import play.api.{Configuration, Environment}
import play.core.SourceMapper

import scala.concurrent.Future

//for compile time dependency injection
class MetricErrorHandler(environment: Environment,
                         configuration: Configuration,
                         sourceMapper: Option[SourceMapper] = None,
                         router: => Option[Router] = None) extends DefaultHttpErrorHandler(environment,configuration,sourceMapper,router){
  import play.api.mvc._
  import codacy.metrics.dropwizard.{Result => _, _}

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    if(! excludeRequest(request)) mark(configuration.failedRequests)
    super.onClientError(request,statusCode,message)
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    if(! excludeRequest(request)) mark(configuration.failedRequests)
    super.onServerError(request,exception)
  }
}

