import Dependencies._

name := """play-dropwizard"""

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

organization := "com.codacy"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies ++= Seq(
  metricsCore,
  metricsJson,
  metricsServlets,
  play,
  playJdbc,
  playWs
)