package codacy.metrics.dropwizard

object HealthCheckRegistry{
  private[dropwizard] val self = new com.codahale.metrics.health.HealthCheckRegistry
}
