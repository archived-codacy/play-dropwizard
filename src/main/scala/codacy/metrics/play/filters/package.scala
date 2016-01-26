package codacy.metrics.play

import play.api.http.Status._
import play.api.mvc.{RequestHeader, Result}

package object filters {

  implicit class ResultExtensions(result:Result){
    def is2xx: Boolean = (200 until 300).contains(result.header.status)
    def isSuccess:Boolean = is2xx || Set(FOUND,NOT_MODIFIED).contains(result.header.status)
  }

  private[filters] implicit val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  private[filters] def dottedPath(requestHeader:RequestHeader) = {
    s"path${requestHeader.path.replaceAll("""/""",""".""")}"
  }
}
