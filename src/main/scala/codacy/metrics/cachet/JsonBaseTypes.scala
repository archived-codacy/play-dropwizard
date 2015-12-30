package codacy.metrics.cachet

import ComponentStatus._
import IncidentStatus._
import play.api.data.validation.ValidationError
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.{Success, Try}

private[cachet] trait JsonBaseTypes{

  private[this] val IntOrStringToIntReads: Reads[Int] = IntReads.orElse(
    StringReads.map{ case raw => Try(raw.toInt) }.collect(ValidationError("not a valid number")){
      case Success(i) => i
  })

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

  implicit val componentIdFormat: Format[ComponentId] = Format[ComponentId](
    LongReads.map( ComponentId.apply ),
    Writes( (id:ComponentId) => Json.toJson(id.value) )
  )

  implicit val ComponentNameFormat = Format(
    StringReads.map( ComponentName.apply ), Writes( (value:ComponentName) => Json.toJson(value.value) )
  )
  implicit val ComponentDescriptionFormat = Format(
    StringReads.map( ComponentDescription.apply ), Writes( (value:ComponentDescription) => Json.toJson(value.value) )
  )
  implicit val ComponentLinkFormat = Format(
    StringReads.map( ComponentLink.apply ), Writes( (value:ComponentLink) => Json.toJson(value.value) )
  )
  implicit val ComponentOrderFormat = Format(
    IntReads.map( ComponentOrder.apply ), Writes( (value:ComponentOrder) => Json.toJson(value.value) )
  )
  implicit val GroupIdFormat = Format(
    IntReads.map( GroupId.apply ), Writes( (value:GroupId) => Json.toJson(value.value) )
  )
  implicit val GroupNameFormat = Format(
    StringReads.map( GroupName.apply ), Writes( (value:GroupName) => Json.toJson(value.value) )
  )
  implicit val GroupOrderFormat = Format(
    IntReads.map( GroupOrder.apply ), Writes( (value:GroupOrder) => Json.toJson(value.value) )
  )
  implicit val IncidentIdFormat = Format(
    LongReads.map( IncidentId.apply ), Writes( (value:IncidentId) => Json.toJson(value.value) )
  )
  implicit val IncidentNameFormat = Format(
    StringReads.map( IncidentName.apply ), Writes( (value:IncidentName) => Json.toJson(value.value) )
  )
  implicit val IncidentMessageFormat = Format(
    StringReads.map( IncidentMessage.apply ), Writes( (value:IncidentMessage) => Json.toJson(value.value) )
  )
  implicit val IncidentVisibilityFormat = Format(
    IntReads.map( IncidentVisibility.apply ), Writes( (value:IncidentVisibility) => Json.toJson(value.value) )
  )
  implicit val SubscriberEmailFormat = Format(
    StringReads.map( SubscriberEmail.apply ), Writes( (value:SubscriberEmail) => Json.toJson(value.value) )
  )
  implicit val SubscriberVerifyFormat = Format(
    IntReads.map( SubscriberVerify.apply ), Writes( (value:SubscriberVerify) => Json.toJson(value.value) )
  )
  implicit val MetricIdFormat = Format(
    IntReads.map( MetricId.apply ), Writes( (value:MetricId) => Json.toJson(value.value) )
  )
  implicit val MetricNameFormat = Format(
    StringReads.map( MetricName.apply ), Writes( (value:MetricName) => Json.toJson(value.value) )
  )
  implicit val MetricSuffixFormat = Format(
    StringReads.map( MetricSuffix.apply ), Writes( (value:MetricSuffix) => Json.toJson(value.value) )
  )
  implicit val MetricDescriptionFormat = Format(
    StringReads.map( MetricDescription.apply ), Writes( (value:MetricDescription) => Json.toJson(value.value) )
  )
  implicit val MetricDefaultValueFormat = Format(
    LongReads.map( MetricDefaultValue.apply ), Writes( (value:MetricDefaultValue) => Json.toJson(value.value) )
  )
  implicit val PointIdFormat: Format[PointId] = Format(
    LongReads.map( PointId.apply ), Writes( (value:PointId) => Json.toJson(value.value) )
  )
  implicit val PointValueFormat: Format[PointValue] = Format(
    LongReads.map( PointValue.apply ), Writes( (value:PointValue) => Json.toJson(value.value) )
  )
}
