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
  aggregate(macroSub).
  settings(
    // include the macro classes and resources in the main jar
    mappings in (Compile, packageBin) <++= mappings in (macroSub, Compile, packageBin),
    // include the macro sources in the main source jar
    mappings in (Compile, packageSrc) <++= mappings in (macroSub, Compile, packageSrc)
  )

lazy val macroSub = (project in file("macro")).
  settings(commonSettings: _*).
  settings( libraryDependencies += scalaReflect.value ).
  settings( libraryDependencies += playJson ).
  settings(
    publish := {},
    publishLocal := {}
  )

organizationName := "Codacy"

organizationHomepage := Some(new URL("https://www.codacy.com"))

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

startYear := Some(2016)

description := "dropwizard-metrics bindings for scala and play-framework"

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/codacy/play-dropwizard.git"))

pomExtra :=
  <scm>
    <url>https://github.com/codacy/play-dropwizard.git</url>
    <connection>scm:git:git@github.com:codacy/play-dropwizard.git</connection>
    <developerConnection>scm:git:https://github.com/codacy/play-dropwizard.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>johannegger</id>
      <name>Johann</name>
      <email>johann [at] codacy.com</email>
      <url>https://github.com/johannegger</url>
    </developer>
  </developers>