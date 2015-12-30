package codacy.filters

import codacy.metrics.dropwizard._
import play.api.mvc.{Result, RequestHeader, Filter}
import scala.concurrent.Future

case class SuccessfulRequests(successfulRequestsMeter: MeterName, excludeRequest: RequestHeader => Boolean) extends Filter{

  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val result = nextFilter(requestHeader)

    if(! excludeRequest(requestHeader)){
      result.onSuccess{
        case result if result.is2xx =>
          mark(successfulRequestsMeter)
        case _ =>
          ()
      }
    }

    result
  }
}
