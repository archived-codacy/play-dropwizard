package codacy.metrics

import com.codahale.metrics
import com.codahale.metrics.health

package object dropwizard extends TypesApi with MetricRegistryApi with HealthCheckRegistryApi{
  import scala.language.implicitConversions
  implicit def toUnderlying(metricRegistry: MetricRegistry ): metrics.MetricRegistry = metricRegistry.underlying
  implicit def toUnderlying(healthCheckRegistry: HealthCheckRegistry ): health.HealthCheckRegistry = healthCheckRegistry.underlying
}

package dropwizard{

  case class HealthCheckName(value:String) extends AnyVal
  case class MeterName(value:String) extends AnyVal
  case class TimerName(value:String) extends AnyVal
}