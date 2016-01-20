
name := """play-dropwizard"""

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

lazy val scalaReflect = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }

libraryDependencies ++= Seq(
  Dependencies.metricsCore,
  Dependencies.metricsJson,
  Dependencies.metricsServlets,
  Dependencies.metricsGraphite,
  Dependencies.play,
  Dependencies.playJdbc,
  Dependencies.playWs
)

lazy val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "codacy",
  version      := "0.1.11",
  addCompilerPlugin(Dependencies.macroParadise cross CrossVersion.full)
) ++ CodacySbt.autoImport.privateMvnPublish ++ CodacySbt.autoImport.privateMvnResolver

lazy val core = (project in file(".")).
  dependsOn(macroSub % "compile-internal").
  settings(commonSettings: _*).
  settings(
    // include the macro classes and resources in the main jar
    mappings in (Compile, packageBin) <++= mappings in (macroSub, Compile, packageBin),
    // include the macro sources in the main source jar
    mappings in (Compile, packageSrc) <++= mappings in (macroSub, Compile, packageSrc)
  )

lazy val macroSub = (project in file("macro")).
  settings(commonSettings: _*).
  settings( libraryDependencies += scalaReflect.value ).
  settings( libraryDependencies += Dependencies.playJson ).
  settings(
    publish := {},
    publishLocal := {}
  )

