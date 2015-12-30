package codacy.metrics.cachet

import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

private[cachet] trait MetricFormat{

  implicit val createMetricW: OWrites[CreateMetric] = metricFieldsFmt.asOWrites.contramap( (_:CreateMetric).fields )

  implicit val responseMetricR: Reads[ResponseMetric] = {
    import scala.language.postfixOps
    (metricFieldsFmt:Reads[MetricFields]) and
    (__ \ "points").readNullable(implicitly[Reads[List[ResponsePoint]]]) tupled
  }.collect(ValidationError("wrong metric fields")){
    case (MetricFields(Some(id),Some(name),Some(suffix),Some(desc),Some(default),
    display,Some(created),updated),points) =>
      ResponseMetric(id,name,suffix,desc,default,display,created,updated,points.getOrElse(List.empty))
  }

  private[this] case class MetricFields(id:Option[MetricId], name: Option[MetricName],
                                        suffix:Option[MetricSuffix], description: Option[MetricDescription],
                                        default_value:Option[MetricDefaultValue],
                                        display_chart:Option[Boolean]=None, created_at:Option[Date]=None,
                                        updated_at:Option[Date]=None)

  private[this] lazy val metricFieldsFmt = Json.format[MetricFields]

  private[this] implicit class CreateMetricExtension(value:CreateMetric){
    import value._
    def fields = MetricFields(None,Option(name),Option(suffix),Option(description),Option(defaultValue),Option(displayChart))
  }
}
