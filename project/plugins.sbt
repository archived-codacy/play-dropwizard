resolvers ++= Seq(
  Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
  "Era7 maven releases" at "http://releases.era7.com.s3.amazonaws.com"
)

addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.13.1")

addSbtPlugin("com.codacy" % "codacy-sbt-plugin" % "4.0.16")

lazy val root: Project = project.in(file(".")).dependsOn(codacySbt)

lazy val codacySbt = uri("ssh://git@bitbucket.org/qamine/codacy-sbt.git#4.0.16")

