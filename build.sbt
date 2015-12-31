import Dependencies._

name := """play-dropwizard"""

version := "0.1.0-SNAPSHOT"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

lazy val scalaReflect = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }

libraryDependencies ++= Seq(
  metricsCore,
  metricsJson,
  metricsServlets,
  play,
  playJdbc,
  playWs
)

lazy val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "com.codacy"
)

lazy val core = (project in file(".")).
  dependsOn(macroSub).
  settings(commonSettings: _*).
  settings(
    addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
  )

lazy val macroSub = (project in file("macro")).
  settings(commonSettings: _*).
  settings(
    addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full),
    libraryDependencies += scalaReflect.value
    // other settings here
  )