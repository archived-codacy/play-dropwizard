package codacy.metrics.cachet

sealed trait Point

case class PointId(value:Long)    extends Identifier[Point]{ override def toString = value.toString }
case class PointValue(value:Long) extends AnyVal

case class CreatePoint(value:PointValue,timestamp:Option[Date]) extends Point
case class ResponsePoint(id:PointId, metricId:MetricId, value:PointValue,createdAt:Date,
                         updatedAt:Option[Date]) extends Point

