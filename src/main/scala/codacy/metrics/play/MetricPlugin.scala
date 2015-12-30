package codacy.metrics.play

import codacy.healthChecks.DatabaseHealthCheck
import codacy.metrics.dropwizard._
import play.api.db.{DBApi, DBPlugin}
import play.api.{Application, Plugin}

class MetricPlugin(app: Application) extends Plugin{ self =>

  override def onStart():Unit = {
    app.plugin[DBPlugin].map(_.api).map(initDbHealthChecks)
  }

  private[this] def initDbHealthChecks(dBApi: DBApi) = {
    dBApi.datasources.foreach{ case (database,name) =>
      val healthCheck = DatabaseHealthCheck(database)
      val checkName = HealthCheckName(s"$databasePrefix:${name}")
      register(checkName,healthCheck)
    }
  }
}