package codacy.healthChecks

import com.codahale.metrics.health.HealthCheck
import play.api.db.Database

import scala.util.{Failure, Success, Try}

case class DatabaseHealthCheck(database:Database) extends HealthCheck{
  override def check() = {
    Try( database.getConnection().close/*(5seconds)*/ ) match{
      case Success(_) =>
        HealthCheck.Result.healthy()
      case Failure(ex) =>
        HealthCheck.Result.unhealthy(ex.getMessage)
    }
  }
}
