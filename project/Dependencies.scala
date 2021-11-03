import sbt._

object Dependencies {

  val finchVersion = "0.32.1"
  val finch = Seq(
    "com.github.finagle" %% "finchx-core" % finchVersion,
    "com.github.finagle" %% "finchx-circe" % finchVersion,
    "io.github.felixbr" %% "finagle-http-effect" % "0.3.0",
    "io.chrisdavenport" %% "log4cats-slf4j" % "1.1.1",
    "com.samstarling" %% "finagle-prometheus" % "0.0.15"
  )

  val circeVersion = "0.14.1"
  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-parser",
  ).map(_ % circeVersion)

  val logging = Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
    "ch.qos.logback" % "logback-classic" % "1.2.6"
  )

  val scalaCacheVersion = "0.28.0"
  val scalaCache = Seq(
    "com.github.cb372" %% "scalacache-caffeine" % scalaCacheVersion,
    "com.github.cb372" %% "scalacache-cats-effect" % scalaCacheVersion,
  )

  val sttpVersion = "3.3.16"
  val etc = Seq(
    "com.github.pureconfig" %% "pureconfig" % "0.17.0",
  )


  val scalaTestVersion = "3.2.10"
  val mockserverVersion = "5.11.2"
  val specs = Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion %  Test,
    "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test,
    "org.mock-server" % "mockserver-client-java" % mockserverVersion % Test,
    "org.mock-server" % "mockserver-netty" % mockserverVersion % Test,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.2"  % Test,
  )

  val all: Seq[ModuleID] = finch ++ circe ++ logging ++ scalaCache ++ etc ++ specs
}
