package codacy.json

import java.util
import java.util.concurrent.TimeUnit
import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.{MetricRegistry, MetricFilter}
import com.codahale.metrics.json.{MetricsModule, HealthCheckModule}
import com.fasterxml.jackson.databind.{ObjectWriter, ObjectMapper}
import play.api.libs.json._

import scala.util.{Success, Try}

trait DropwizardJson {

  type HealthCheckResult = util.SortedMap[String, HealthCheck.Result]

  private[this] val healthOM: ObjectMapper = new ObjectMapper().registerModule(new HealthCheckModule())
  private[this] val healthOW:ObjectWriter = healthOM.writer()

  private[this] val metricsOM: ObjectMapper = new ObjectMapper().registerModule(
    new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, true, MetricFilter.ALL)
  )
  private[this] val metricsOW:ObjectWriter = metricsOM.writer()

  implicit val metricRegistryWriter = OWrites{ (obj:MetricRegistry) =>
    val bytes = metricsOW.writeValueAsBytes(obj)
    Try(Json.parse(bytes)).map(_.validate[JsObject]) match{
      case Success(JsSuccess(jsObject,_)) =>
        jsObject
      case _ => Json.obj()
    }
  }

  implicit val healthCheckWriter = OWrites[HealthCheckResult]{ (obj:HealthCheckResult) =>
    val bytes = healthOW.writeValueAsBytes(obj)
    Try(Json.parse(bytes)).map(_.validate[JsObject]) match{
      case Success(JsSuccess(jsObject,_)) =>
        jsObject
      case _ => Json.obj()
    }
  }
}
