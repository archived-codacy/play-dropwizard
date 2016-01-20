package codacy.metrics

import com.codahale.metrics
import com.codahale.metrics.health

package object dropwizard
  extends TypesApi
  with MetricRegistryApi
  with HealthCheckRegistryApi
  with Helpers{

  import scala.language.implicitConversions
  private[metrics] implicit def toUnderlying(metricRegistry: MetricRegistry ): metrics.MetricRegistry = metricRegistry.underlying
  private[metrics] implicit def toUnderlying(healthCheckRegistry: HealthCheckRegistry ): health.HealthCheckRegistry = healthCheckRegistry.underlying
}
