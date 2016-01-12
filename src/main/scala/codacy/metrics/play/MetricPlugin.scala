package codacy.metrics.play

import codacy.healthChecks.DatabaseHealthCheck
import codacy.metrics.dropwizard._
import javax.inject._
import com.zaxxer.hikari.HikariDataSource
import play.api.{Configuration, Environment}
import play.api.db.DBApi
import play.api.inject.Module
import com.codahale.metrics.health.{HealthCheckRegistry => DropwizardHealthCheckRegistry}
import com.codahale.metrics.{MetricRegistry => DropwizardMetricRegistry}

trait DatabaseHealthChecks

@Singleton
class DatabaseHealthCheckImpl @Inject() (dbApi: DBApi,configuration: Configuration) extends DatabaseHealthChecks{
  initDbHealthChecks(dbApi)

  private[this] def initDbHealthChecks(dBApi: DBApi) = {
    dBApi.databases().foreach{ case database =>
      database.dataSource match{
        case ds:HikariDataSource =>
          val hReg:DropwizardHealthCheckRegistry = HealthCheckRegistry
          ds.setHealthCheckRegistry(hReg)
          if (configuration.databaseMetrics){
            val mReg:DropwizardMetricRegistry = MetricRegistry
            ds.setMetricRegistry(mReg)
          }
        case _ =>
          val healthCheck = DatabaseHealthCheck(database)
          val checkName = HealthCheckName(s"$databasePrefix${database.name}")
          register(checkName,healthCheck)
      }
    }
  }
}

class DatabaseHealthCheckModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[DatabaseHealthChecks].to[DatabaseHealthCheckImpl].eagerly()
  )
}