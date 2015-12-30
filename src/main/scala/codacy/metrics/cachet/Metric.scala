package codacy.metrics.cachet

sealed trait Metric

case class MetricId(value:Int)               extends Identifier[Metric]{ override def toString = value.toString }
case class MetricName(value:String)          extends AnyVal
case class MetricSuffix(value:String)        extends AnyVal
case class MetricDescription(value:String)   extends AnyVal
case class MetricDefaultValue(value:Long)    extends AnyVal
//case class MetricDisplayChart(value:Boolean) extends AnyVal

case class CreateMetric(name:MetricName, suffix:MetricSuffix, description: MetricDescription,
                        defaultValue:MetricDefaultValue, displayChart:Boolean=false) extends Metric

case class ResponseMetric(id: MetricId, name: MetricName, suffix:MetricSuffix,
                          description: MetricDescription, defaultValue:MetricDefaultValue,
                          displayChart:Option[Boolean], createdAt: Date,
                          updatedAt: Option[Date]=None, points:List[ResponsePoint]) extends Metric

