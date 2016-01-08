import Dependencies._

name := """play-dropwizard"""

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

lazy val scalaReflect = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }

libraryDependencies ++= Seq(
  metricsCore,
  metricsJson,
  metricsServlets,
  metricsGraphite,
  play,
  playJdbc,
  playWs
)

lazy val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "com.codacy",
  version      := "0.1.0-SNAPSHOT",
  addCompilerPlugin(macroParadise cross CrossVersion.full)
)

lazy val core = (project in file(".")).
  dependsOn(macroSub).
  settings(commonSettings: _*).
  aggregate(macroSub)

lazy val macroSub = (project in file("macro")).
  settings(commonSettings: _*).
  settings( libraryDependencies += scalaReflect.value )