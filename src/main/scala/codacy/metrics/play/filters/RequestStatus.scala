package codacy.metrics.play.filters

import codacy.metrics.dropwizard._
import play.api.mvc.{Result, Filter, RequestHeader}
import scala.concurrent.Future

case class RequestStatus(excludeRequest: RequestHeader => Boolean) extends Filter{

  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    lazy val result = nextFilter(requestHeader)

    if(! excludeRequest(requestHeader)){
      result.onSuccess{ case response =>
        mark(nameForRequestAndStatus(requestHeader,response.header.status))
      }
    }

    result
  }

  private[this] def nameForRequestAndStatus(requestHeader: RequestHeader,status:Int):MeterName = {
    val dottedPath = s"path${requestHeader.path.replaceAll("""/""",""".""")}"

    MeterName(s"$dottedPath.status.$status")
  }
}
