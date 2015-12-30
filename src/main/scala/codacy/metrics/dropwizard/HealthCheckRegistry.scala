package codacy.metrics.dropwizard

import com.codahale.metrics.health.{HealthCheckRegistry => DropwizardHealthCheckRegistry}
import scala.collection.JavaConversions._

trait HealthCheckRegistry extends HealthCheckRegistryApi{
  private[dropwizard] def underlying:DropwizardHealthCheckRegistry
}

object HealthCheckRegistry extends HealthCheckRegistryApi{
  private[dropwizard] val underlying:DropwizardHealthCheckRegistry = new DropwizardHealthCheckRegistry
}

private[dropwizard] trait HealthCheckRegistryApi{
  import HealthCheckRegistry.underlying

  def register(healthCheckName: HealthCheckName, healthCheck: HealthCheck) =
    underlying.register(healthCheckName.value,healthCheck)

  def names: Set[HealthCheckName] =
    Set.empty ++ underlying.getNames.map(HealthCheckName.apply)

  def runHealthCheck(healthCheckName: HealthCheckName): Result =
    underlying.runHealthCheck(healthCheckName.value)

  def runHealthChecks(): Map[HealthCheckName, Result] =
    JavaMap(underlying.runHealthChecks)
}