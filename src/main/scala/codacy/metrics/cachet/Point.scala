package codacy.metrics.cachet

import codacy.macros.enriched

sealed trait Point

@enriched case class PointId(value:Long)    extends Identifier[Point]
@enriched case class PointValue(value:Long) extends AnyVal

case class CreatePoint(value:PointValue,timestamp:Option[Date]) extends Point
case class ResponsePoint(id:PointId, metricId:MetricId, value:PointValue,createdAt:Date,
                         updatedAt:Option[Date]) extends Point

