package codacy.metrics.play

import codacy.healthChecks.DatabaseHealthCheck
import codacy.metrics.dropwizard._
import javax.inject._
import play.api.{Configuration, Environment}
import play.api.db.DBApi
import play.api.inject.Module

trait DatabaseHealthChecks

@Singleton
class DatabaseHealthCheckImpl @Inject() (dbApi: DBApi) extends DatabaseHealthChecks{
  initDbHealthChecks(dbApi)

  private[this] def initDbHealthChecks(dBApi: DBApi) = {
    dBApi.databases().foreach{ case database =>
      val healthCheck = DatabaseHealthCheck(database)
      val checkName = HealthCheckName(s"$databasePrefix${database.name}")
      register(checkName,healthCheck)
    }
  }
}

class HealthCheckModule extends Module {
  def bindings(environment: Environment,
               configuration: Configuration) = Seq(
    bind[DatabaseHealthChecks].to[DatabaseHealthCheckImpl].eagerly()
  )
}