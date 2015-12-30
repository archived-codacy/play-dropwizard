package codacy.filters

import codacy.metrics.dropwizard._
import play.api.mvc.{Filter, RequestHeader, Result}
import scala.concurrent.Future

case class FailedRequests(failedRequests: MeterName, excludeRequest: RequestHeader => Boolean) extends Filter{

  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    val result = nextFilter(requestHeader)

    if (! excludeRequest(requestHeader)){
      result.onSuccess{
        case result if ! result.isSuccess =>
          mark(failedRequests)
        case _ =>
          ()
      }
    }

    result
  }
}
