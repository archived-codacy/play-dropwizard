package codacy.metrics.cachet

import com.typesafe.config.ConfigFactory
import play.api.{Configuration, Application}
import play.api.libs.ws.ning.{NingAsyncHttpClientConfigBuilder, NingWSClientConfig, NingWSClient}
import play.api.libs.ws._
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

sealed trait Request[Param,Result]{ self =>

  def url: Param => String
  def mapper:WSResponse => Future[Result]
  private[cachet] def f(p:Param): WSRequest => Future[WSResponse]
}

final case class Post[A,B](url: A => String, mapper:WSResponse => Future[B])(implicit w:Writes[A]) extends Request[A,B]{
  private[cachet] def f(payload:A) = (_:WSRequest).post( Json.toJson(payload) )
}

final case class Put[A,B] (url:A => String, mapper:WSResponse => Future[B])(implicit w:Writes[A]) extends Request[A,B]{
  private[cachet] def f(payload:A) = (_:WSRequest).put( Json.toJson(payload) )
}

final case class Get[A,B](url: A => String, mapper:WSResponse => Future[B]) extends Request[A,B]{
  private[cachet] def f(a:A) = (_:WSRequest).get()
}

final case class Delete[A,B](url: A => String, mapper:WSResponse => Future[B]) extends Request[A,B]{
  private[cachet] def f(a:A) = (_:WSRequest).delete()
}

case class ParseException(jsResult:JsError) extends Throwable

private[cachet] trait WsApi{

  def optJsonDataBody[A](implicit reads:Reads[A]): (WSResponse) => Future[Option[A]] = (r:WSResponse) => ( (r.json \ "data").validateOpt[A] match{
    case err:JsError =>
      Future.failed(ParseException(err))
    case JsSuccess(value,_) =>
      Future.successful(value)
  })

  def jsonDataBody[A](implicit reads:Reads[A]) = (r:WSResponse) => ( (r.json \ "data").validate[A] match{
    case err:JsError =>
      Future.failed(ParseException(err))
    case JsSuccess(value,_) =>
      Future.successful(value)
  })

  def statusCheck( f: Int => Boolean ) = ((_:WSResponse).status).andThen(s => Future.successful(f(s)))

  val is204 = statusCheck(_ == 204)

  private[this] def applyRequest[P,R](request:Request[P,R],param:P)(implicit ws: WSClient, ctx: ExecutionContext): Future[R] = {
    (for{
      baseUrl <- CachetConfiguration.cachetUrl
      token   <- CachetConfiguration.cachetToken
    } yield{
      val fullPath = s"$baseUrl${request.url(param)}"
      request.f(param)(ws.url(fullPath).withHeaders("X-Cachet-Token" -> token)).flatMap(request.mapper)
    }).getOrElse(
      Future.failed(new Exception("cachet client not properly configured"))
    )
  }

  implicit class GeneralRequestApplier[Param,Result](request:Request[Param,Result])
                                                    (implicit ws: WSClient, ctx: ExecutionContext) extends (Param => Future[Result]) {
    def apply(param:Param): Future[Result] = applyRequest(request,param)
  }

  implicit class UnitRequestApplier[Result](request:Request[Unit,Result])
                                           (implicit ws: WSClient, executionContext: ExecutionContext) extends (() => Future[Result]) {
    def apply(): Future[Result] = applyRequest(request,())
  }
}