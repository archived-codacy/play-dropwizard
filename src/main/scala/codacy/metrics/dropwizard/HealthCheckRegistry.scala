package codacy.metrics.dropwizard

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import com.codahale.metrics.health.{HealthCheckRegistry => DropwizardHealthCheckRegistry}
import com.codahale.metrics.health.HealthCheck.Result.unhealthy
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.concurrent.{Future, ExecutionContext}

trait HealthCheckRegistry extends HealthCheckRegistryApi{
  private[dropwizard] def underlying:DropwizardHealthCheckRegistry
}

object HealthCheckRegistry extends HealthCheckRegistry{
  private[dropwizard] lazy val underlying:DropwizardHealthCheckRegistry = new DropwizardHealthCheckRegistry
}

private[dropwizard] trait HealthCheckRegistryApi{
  import HealthCheckRegistry.underlying
  import DropwizardExecutionContext.Implicits._

  def register(healthCheckName: HealthCheckName, healthCheck: HealthCheck) =
    underlying.register(healthCheckName.value,healthCheck)

  def names: Set[HealthCheckName] =
    Set.empty ++ underlying.getNames.map(HealthCheckName.apply)

  def runHealthCheck(healthCheckName: HealthCheckName): Result =
    underlying.runHealthCheck(healthCheckName.value)

  def runHealthCheckAsync(healthCheckName: HealthCheckName,timeout:Duration=5.seconds)(implicit actorSystem: ActorSystem): Future[Result] = {
    Future.firstCompletedOf(List(
      Promise.timeout(unhealthy("healthcheck timed out"), timeout),
      Future(underlying.runHealthCheck(healthCheckName.value))
    ))
  }

  def runHealthChecks(): Map[HealthCheckName, Result] =
    JavaMap(underlying.runHealthChecks)

  def runHealthChecksAsync(timeout:Duration=5.seconds)(implicit actorSystem: ActorSystem): Future[Map[HealthCheckName, Result]] = {
    val allTests = names.map{ case name =>
      runHealthCheckAsync(name,timeout).
      recover{ case ex => unhealthy(ex.getMessage) }.
      map{ case result => (name,result) }
    }
    Future.sequence(allTests).map(_.toMap)
  }

}

/* this is a copy of play.api.libs.concurrent.Promise that does not require a running application so we can use it in dependency injection*/
object Promise{
  import scala.util.Try
  import scala.concurrent.{ Future, ExecutionContext, Promise => SPromise }

  def timeout[A](message: => A, duration: scala.concurrent.duration.Duration)(implicit ec: ExecutionContext,actorSystem: ActorSystem): Future[A] = {
    timeout(message, duration.toMillis)
  }

  def timeout[A](message: => A, duration: Long, unit: TimeUnit = TimeUnit.MILLISECONDS)(implicit ec: ExecutionContext,actorSystem: ActorSystem): Future[A] = {
    val p = SPromise[A]()
    actorSystem.scheduler.scheduleOnce(FiniteDuration(duration, unit)) {
      p.complete(Try(message))
    }
    p.future
  }
}