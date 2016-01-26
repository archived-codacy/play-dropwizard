package codacy.metrics.play

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import codacy.metrics.dropwizard.MetricRegistry
import com.codahale.metrics.graphite.{Graphite, GraphiteReporter => DropwizardReporter}
import play.api.Configuration
import scala.concurrent.duration._

class GraphiteReporter(graphiteKeys:GraphiteConfigKeys, configuration:Configuration){

  private[this] lazy val reporter = {
    for{
      hostname <- configuration.graphite.hostname if configuration.graphite.isEnabled
    } yield {

      val playConfPrefix = configuration.graphite.prefix.map(p => s"$p.").getOrElse("")
      //then concat the one passed
      val componentName = graphiteKeys.componentName
      val instanceName  = graphiteKeys.instanceName
      val prefix = s"$playConfPrefix$componentName.$instanceName"

      val graphite = new Graphite(new InetSocketAddress(hostname, configuration.graphite.port))

      DropwizardReporter
        .forRegistry(MetricRegistry)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .prefixedWith( prefix ).build(graphite)
    }
  }

  def start():Unit = start(1.minute)

  def start(interval:Duration): Unit = {
    reporter.map(_.start(interval.toMillis,TimeUnit.MILLISECONDS))
    //TODO: do some logging in case it's not properly configured
  }

  def stop() = reporter.foreach(_.stop())
}
