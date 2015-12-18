package codacy.filters

import com.codahale.metrics.Meter
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future

final class FailedRequests(check:Meter, excludeRequest: RequestHeader => Boolean) extends Filter{

  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val result = nextFilter(requestHeader)

    if (! excludeRequest(requestHeader)){
      result.onSuccess{
        case result if ! result.isSuccess =>
          check.mark()
        case _ =>
          ()
      }
    }

    result
  }
}
