package codacy.metrics.cachet

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

private[cachet] trait PointFormats{
  implicit val createPointW: Writes[CreatePoint] = Json.writes[CreatePoint]

  implicit val responsePointR: Reads[ResponsePoint] = (
    (__ \ "id"        ).read[PointId] and
    (__ \ "metric_id" ).read[MetricId] and
    (__ \ "value"     ).read[PointValue] and
    (__ \ "created_at").read[Date] and
    (__ \ "updated_at").read[Option[Date]]
  )(ResponsePoint.apply _)
}
