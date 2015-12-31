package codacy.metrics.cachet

import ComponentStatus._
import IncidentStatus._
import play.api.data.validation.ValidationError
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.{Success, Try}

private[cachet] trait JsonBaseTypes{
  implicit val componentStatusFormat = Format(
    IntOrStringToIntReads.map( id => ComponentStatus.values.find(_.id == id) ).
      collect(ValidationError("no such component status")){ case Some(status) => status},
    Writes((status:ComponentStatus) => Json.toJson(status.id))
  )

  implicit val IncidentStatusFormat: Format[IncidentStatus.Value] = Format(
    IntOrStringToIntReads.map( id => IncidentStatus.values.find(_.id == id) ).
      collect(ValidationError("no such incident status")){ case Some(status) => status},
    Writes((status:IncidentStatus) => Json.toJson(status.id))
  )

  private[this] lazy val IntOrStringToIntReads: Reads[Int] = IntReads.orElse(
    StringReads.map{ case raw => Try(raw.toInt) }.collect(ValidationError("not a valid number")){
      case Success(i) => i
    }
  )
}
