package codacy.metrics.cachet

import codacy.metrics.cachet.ComponentStatus._
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

private[cachet] trait ComponentFormats{

  implicit val createComponentW: OWrites[CreateComponent] =
    componentFieldsFmt.asOWrites.contramap((_:CreateComponent).fields)

  implicit val updateComponentW: OWrites[UpdateComponent] =
    componentFieldsFmt.asOWrites.contramap((_:UpdateComponent).fields)

  implicit val responseComponentR: Reads[ResponseComponent] = {
    componentFieldsFmt.collect(ValidationError("wrong component fields")){
      case ComponentFields(
        Some(id), Some(name),description, Some(status), link,
        Some(order), groupId, _, Some(createdAt), updatedAt, deletedAt
      ) =>
        val grId = groupId.filter(_.value > 0)
        ResponseComponent(id,name,description,status,link,order,grId,createdAt,updatedAt,deletedAt)
    }
  }

  private[this] case class ComponentFields(id: Option[ComponentId]=None, name: Option[ComponentName]=None,
                                           description:Option[ComponentDescription]=None,
                                           status: Option[ComponentStatus]=None, link: Option[ComponentLink]=None,
                                           order: Option[ComponentOrder]=None, group_id: Option[GroupId]=None,
                                           enabled:Option[Boolean]=None, created_at: Option[Date]=None,
                                           updated_at : Option[Date]=None, deleted_at: Option[Date]=None/*,"status_name": "Operational"*/)


  private[this] lazy val componentFieldsFmt = Json.format[ComponentFields]

  private[this] implicit class CreateComponentExtension(value:CreateComponent){
    import value._
    def fields: ComponentFields = ComponentFields(name = Option(name),description = description, status = Option(status),
      link = link, order = order, group_id = groupId.filter(_.value > 0), enabled = enabled)
  }

  private[this] implicit class UpdateComponentExtension(value:UpdateComponent){
    import value._
    def fields = ComponentFields(id = Option(id), name = name, status = status,link = link,
      order = order, group_id = groupId.filter(_.value > 0))
  }
}
