import sbt._

object Dependencies{

  val dropWizardVersion = "3.1.2"
  val playVersion = "2.4.6"

  val metricsCore     = "io.dropwizard.metrics" %  "metrics-core"     % dropWizardVersion
  val metricsJson     = "io.dropwizard.metrics" %  "metrics-json"     % dropWizardVersion
  val metricsServlets = "io.dropwizard.metrics" %  "metrics-servlets" % dropWizardVersion
  val play            = "com.typesafe.play"     %% "play"             % playVersion
  val playJdbc        = "com.typesafe.play"     %% "play-jdbc"        % playVersion
  val playWs          = "com.typesafe.play"     %% "play-ws"          % playVersion
  val macroParadise   = "org.scalamacros"       %% "paradise"         % "2.1.0"

}

