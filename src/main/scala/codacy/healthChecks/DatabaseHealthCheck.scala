package codacy.healthChecks

import javax.sql.DataSource

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result

import scala.util.{Success, Try}

final class DatabaseHealthCheck(database:DataSource) extends HealthCheck{
  override def check(): Result = {
    Try( database.getConnection().isValid(5/*seconds*/) ) match{
      case Success(true) =>
        HealthCheck.Result.healthy()
      case _ =>
        HealthCheck.Result.unhealthy("did not respond")
    }
  }
}
