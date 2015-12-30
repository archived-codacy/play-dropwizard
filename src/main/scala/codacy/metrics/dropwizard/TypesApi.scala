package codacy.metrics.dropwizard

import com.codahale.metrics.health.HealthCheck.{Result => HealthCheckResult}

trait TypesApi {
  type Timer               = com.codahale.metrics.Timer
  type HealthCheck         = com.codahale.metrics.health.HealthCheck
  type HealthCheckResults  = Map[HealthCheckName, HealthCheckResult]
  type Result              = com.codahale.metrics.health.HealthCheck.Result
}
