package codacy.plugin

import codacy.healthChecks.DatabaseHealthCheck
import com.codahale.metrics.{Meter, MetricRegistry}
import com.codahale.metrics.health.HealthCheckRegistry
import play.api.db.{DBApi, DBPlugin}
import play.api.{Application, Plugin}

trait MetricApi{

  def metricRegistry:MetricRegistry
  def healthCheckRegistry:HealthCheckRegistry
}

trait MetricPlugin extends Plugin{
  def api:MetricApi
}

class DefaultMetricPlugin(app: Application) extends MetricPlugin{ self =>

  override def onStart():Unit = {
    app.plugin[DBPlugin].map(_.api).map(initDbHealthChecks)
    println(s"metrics plugin started")
  }

  override lazy val api: MetricApi = new MetricApi{
    override lazy val metricRegistry: MetricRegistry           = new MetricRegistry
    override lazy val healthCheckRegistry: HealthCheckRegistry = new HealthCheckRegistry
  }

  private[this] def initDbHealthChecks(dBApi: DBApi) = {
    dBApi.datasources.foreach{ case (database,name) =>
      val healthCheck = new DatabaseHealthCheck(database)

      api.healthCheckRegistry.register(s"$databasePrefix:${name}",healthCheck)
    }
  }
}

object Metrics{

  private[this] def error = throw new Exception("Metric plugin is not registered.")

  def metricRegistry(implicit app:Application):MetricRegistry =
    app.plugin[MetricPlugin].map(_.api.metricRegistry).getOrElse(error)

  def healthCheckRegistry(implicit app:Application):HealthCheckRegistry =
    app.plugin[MetricPlugin].map(_.api.healthCheckRegistry).getOrElse(error)
}