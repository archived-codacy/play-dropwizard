package codacy.filters

import codacy.plugin.Metrics
import play.api.mvc.{Filter, RequestHeader, Result}
import scala.concurrent.Future

final class FailedRequests(meterName: => String, excludeRequest: RequestHeader => Boolean) extends Filter{

  private[this] lazy val check = {
    import play.api.Play.current
    Metrics.metricRegistry.meter(meterName)
  }

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
