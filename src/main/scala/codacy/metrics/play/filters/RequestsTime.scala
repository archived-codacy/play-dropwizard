package codacy.metrics.play.filters

import codacy.metrics.dropwizard._
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future

case class RequestsTime(excludeRequest: RequestHeader => Boolean) extends Filter{

  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    lazy val result = nextFilter(requestHeader)

    if(! excludeRequest(requestHeader)){
      timedAsync(nameForRequest(requestHeader))(result)
    }

    result
  }

  private[this] def nameForRequest(requestHeader: RequestHeader):TimerName = {
    TimerName(dottedPath(requestHeader))
  }
}
