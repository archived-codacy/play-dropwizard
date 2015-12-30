package codacy.metrics.cachet

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

private[cachet] trait GroupFormats{

  implicit val createGroupW: Writes[CreateGroup] = Json.writes[CreateGroup]

  implicit val updateGroupW: Writes[UpdateGroup] = Json.writes[UpdateGroup]

  implicit val responseGroupR: Reads[ResponseGroup] = (
    (__ \ "id"        ).read[GroupId] and
    (__ \ "name"      ).read[GroupName] and
    (__ \ "order"     ).read[GroupOrder] and
    (__ \ "created_at").read[Date] and
    (__ \ "updated_at").read[Option[Date]]
  )( ResponseGroup.apply _)
}
