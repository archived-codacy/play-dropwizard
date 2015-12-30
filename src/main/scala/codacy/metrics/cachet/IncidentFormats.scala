package codacy.metrics.cachet

import codacy.metrics.cachet.ComponentStatus._
import codacy.metrics.cachet.IncidentStatus.IncidentStatus
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

private[cachet] trait IncidentFormats{

  implicit val responseIncidenR: Reads[ResponseIncident] = Json.reads[IncidentFields].collect(ValidationError("wrong incident fields")){
    case IncidentFields(Some(id),Some(compId),Some(name),Some(status),Some(visible),
                        Some(message),Some(sDate),Some(cDate),uDate,dDate,_) =>
      ResponseIncident(id,compId,name,status,visible,message,sDate,cDate,uDate,dDate)
  }

  implicit val createIncidenW: Writes[CreateIncident] =
    incidentFieldsWrites.contramap( (obj:CreateIncident) => (obj.fields,Some(obj.notifySubscribers)) )

  implicit val updateIncidenW: Writes[UpdateIncident] =
    incidentFieldsWrites.contramap( (obj:UpdateIncident) => (obj.fields,obj.notifySubscribers) )

  private[this] case class IncidentFields(id:Option[IncidentId],component_id:Option[ComponentId], name:Option[IncidentName],
                                          status:Option[IncidentStatus], visible:Option[IncidentVisibility],
                                          message: Option[IncidentMessage], scheduled_at:Option[Date]=None,
                                          created_at:Option[Date]=None, updated_at:Option[Date]=None,
                                          deleted_at:Option[Date]=None,component_status:Option[ComponentStatus]=None)


  private[this] lazy val incidentFieldsWrites = {
    import scala.language.postfixOps
    OWrites( (f:IncidentFields) =>
      Json.writes[IncidentFields].writes(f) match{
        case obj:JsObject => obj
        case _ => Json.obj()
      }
    ) and (__ \ "notify").writeNullable[Boolean] tupled
  }

  private[this] implicit class IncidentExtension(value:CreateIncident){
    import value._
    def fields = IncidentFields(
      None,componentId,Option(name),Option(status),Option(visible),Option(message),component_status = componentStatus
    )
  }

  private[this] implicit class UpdateIncidentExtension(value:UpdateIncident){
    import value._
    def fields = IncidentFields(Some(id),componentId,name,status,visible,message)
  }
}