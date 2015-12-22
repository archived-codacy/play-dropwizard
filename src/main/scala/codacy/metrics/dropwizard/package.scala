package codacy.metrics

import com.codahale.metrics
import com.codahale.metrics.health

package object dropwizard extends TypesApi{

  implicit def toUnderlying(metricRegistry: MetricRegistry): metrics.MetricRegistry = metricRegistry.self
  implicit def toUnderlying(healthCheckRegistry: HealthCheckRegistry): health.HealthCheckRegistry = healthCheckRegistry.self

  implicit class MeterExtensions(meterName: MeterName){
    def mark = MetricRegistry.meter(meterName.value).mark()
  }
}