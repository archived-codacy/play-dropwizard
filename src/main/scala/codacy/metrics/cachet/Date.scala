package codacy.metrics.cachet

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.util.{Failure, Success, Try}


case class Date(value:DateTime) extends AnyVal

object Date{
  import scala.language.implicitConversions

  implicit def fromValue(date:DateTime): Date = Date(date)
  implicit def toValue(date:Date): DateTime = date.value

  private[this] val dateFmt = DateTimeFormat.forPattern("yyyy-MM-dd H:m:s")

  implicit val fmt:Format[Date] = Format(
    Reads.StringReads.flatMap{ case raw =>
      Reads(_ =>
        Try(dateFmt.parseDateTime(raw)) match{
          case Success(date) => JsSuccess(date)
          case Failure(err) => JsError(ValidationError(err.getMessage,raw))
        }
      )
    },
    Writes(date => Json.toJson(date.getMillis / 1000))
  )
}