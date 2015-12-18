package codacy.filters

import com.codahale.metrics.{Meter}
import play.api.mvc.{Result, RequestHeader, Filter}

import scala.concurrent.Future

final class SuccessfulRequests(check:Meter, excludeRequest: RequestHeader => Boolean) extends Filter{

  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val result = nextFilter(requestHeader)

    if(! excludeRequest(requestHeader)){
      result.onSuccess{
        case result if result.is2xx =>
          check.mark()
        case _ =>
          ()
      }
    }

    result
  }
}
