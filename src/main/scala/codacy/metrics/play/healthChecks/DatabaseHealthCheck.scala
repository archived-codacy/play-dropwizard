package codacy.healthChecks

import javax.sql.DataSource
import com.codahale.metrics.health.HealthCheck

import scala.util.{Success, Try}

case class DatabaseHealthCheck(database:DataSource) extends HealthCheck{
  override def check() = {
    Try( database.getConnection().isValid(5/*seconds*/) ) match{
      case Success(true) =>
        HealthCheck.Result.healthy()
      case _ =>
        HealthCheck.Result.unhealthy("did not respond")
    }
  }
}
