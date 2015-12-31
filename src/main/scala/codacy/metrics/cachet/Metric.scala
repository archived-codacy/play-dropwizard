package codacy.metrics.cachet

import codacy.macros.enriched

sealed trait Metric

@enriched case class MetricId(value:Int)             extends Identifier[Metric]
@enriched case class MetricName(value:String)        extends AnyVal
@enriched case class MetricSuffix(value:String)      extends AnyVal
@enriched case class MetricDescription(value:String) extends AnyVal
@enriched case class MetricDefaultValue(value:Long)  extends AnyVal

case class CreateMetric(name:MetricName, suffix:MetricSuffix, description: MetricDescription,
                        defaultValue:MetricDefaultValue, displayChart:Boolean=false) extends Metric

case class ResponseMetric(id: MetricId, name: MetricName, suffix:MetricSuffix,
                          description: MetricDescription, defaultValue:MetricDefaultValue,
                          displayChart:Option[Boolean], createdAt: Date,
                          updatedAt: Option[Date]=None, points:List[ResponsePoint]) extends Metric

