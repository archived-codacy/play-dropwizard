package codacy.metrics.dropwizard

import com.codahale.metrics.health.{HealthCheckRegistry => DropwizardHealthCheckRegistry}
import com.codahale.metrics.health.HealthCheck.Result.unhealthy
import play.api.libs.concurrent.Promise
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.concurrent.{Future, ExecutionContext}

trait HealthCheckRegistry extends HealthCheckRegistryApi{
  private[dropwizard] def underlying:DropwizardHealthCheckRegistry
}

object HealthCheckRegistry extends HealthCheckRegistry{
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

  def runHealthCheckAsync(healthCheckName: HealthCheckName,timeout:Duration=5.seconds)(implicit executionContext: ExecutionContext): Future[Result] = {
    Future.firstCompletedOf(List(
      Promise.timeout(unhealthy("healthcheck timed out"), timeout),
      Future(underlying.runHealthCheck(healthCheckName.value))
    ))
  }

  def runHealthChecks(): Map[HealthCheckName, Result] =
    JavaMap(underlying.runHealthChecks)

  def runHealthChecksAsync(timeout:Duration=5.seconds)(implicit executionContext: ExecutionContext): Future[Map[HealthCheckName, Result]] = {
    val allTests = names.map{ case name =>
      runHealthCheckAsync(name,timeout).
      recover{ case ex => unhealthy(ex.getMessage) }.
      map{ case result => (name,result) }
    }
    Future.sequence(allTests).map(_.toMap)
  }

}