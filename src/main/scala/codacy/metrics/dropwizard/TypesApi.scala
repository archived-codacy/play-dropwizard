package codacy.metrics.dropwizard

trait TypesApi {
  type MetricRegistry      = MetricRegistry.type
  type HealthCheckRegistry = HealthCheckRegistry.type
  type Timer               = com.codahale.metrics.Timer
  type HealthCheck         = com.codahale.metrics.health.HealthCheck
}
