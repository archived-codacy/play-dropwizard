package codacy.json

import java.util.concurrent.TimeUnit
import com.codahale.metrics.{MetricRegistry => DropwizardRegistry, MetricFilter}
import com.codahale.metrics.json.{MetricsModule, HealthCheckModule}
import com.fasterxml.jackson.databind.{ObjectWriter, ObjectMapper}
import play.api.libs.json._
import codacy.metrics.dropwizard._
import scala.util.{Success, Try}

trait DropwizardJson {

  implicit val metricRegistryWriter: OWrites[MetricRegistry] = OWrites{ (obj:MetricRegistry) =>
    val underlying:DropwizardRegistry = obj
    val bytes = metricsOW.writeValueAsBytes(underlying)
    parseAsJsObject(bytes)
  }

  implicit val healthCheckWriter = OWrites[HealthCheckResults]{ (obj:HealthCheckResults) =>
    obj match{
      case map:JavaMap =>
        val bytes = healthOW.writeValueAsBytes(map.javaRepr)
        parseAsJsObject(bytes)
      case _ => Json.obj()
    }
  }

  private[this] val healthOM: ObjectMapper = new ObjectMapper().registerModule(new HealthCheckModule())
  private[this] val healthOW:ObjectWriter = healthOM.writer()

  private[this] val metricsOM: ObjectMapper = new ObjectMapper().registerModule(
    new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, true, MetricFilter.ALL)
  )
  private[this] val metricsOW:ObjectWriter = metricsOM.writer()

  private[this] def parseAsJsObject(bytes: Array[Byte]): JsObject = {
    Try(Json.parse(bytes)).map(_.validate[JsObject]) match{
      case Success(JsSuccess(jsObject,_)) =>
        jsObject
      case _ => Json.obj()
    }
  }
}
