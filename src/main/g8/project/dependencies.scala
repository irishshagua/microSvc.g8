import sbt._

object Dependencies {

  val akkaVersion          = "2.5.6"
  val akkaHttpVersion      = "10.0.6"
  val catsVersion          = "1.0.0-RC1"
  val slf4jVersion         = "1.7.25"
  val logbackVersion       = "1.2.3"
  val scalaTestsVersion    = "3.0.5"
  val refinedVersion       = "0.8.7"
  val pureConfigV          = "0.9.1"
  val akkaHttpCirceVersion = "1.20.0-RC2"
  val circeVersion         = "0.9.1"
  val mockitoVersion       = "2.8.9"
  val sttpVersion          = "1.1.12"

  val akkaActor       = "com.typesafe.akka"          %% "akka-actor"            % akkaVersion
  val akkaTestKit     = "com.typesafe.akka"          %% "akka-testkit"          % akkaVersion % Test
  val akkaStreams     = "com.typesafe.akka"          %% "akka-stream"           % akkaVersion
  val cats            = "org.typelevel"              %% "cats-core"             % catsVersion
  val akkaHttpCore    = "com.typesafe.akka"          %% "akka-http-core"        % akkaHttpVersion
  val akkaHttp        = "com.typesafe.akka"          %% "akka-http"             % akkaHttpVersion
  val akkaHttpTestkit = "com.typesafe.akka"          %% "akka-http-testkit"     % akkaHttpVersion % IntegrationTest
  val akkaSlf4j       = "com.typesafe.akka"          %% "akka-slf4j"            % akkaVersion // Akka SLF4J
  val logback         = "ch.qos.logback"             % "logback-classic"        % logbackVersion // SLF4J backend
  val slf4j           = "org.slf4j"                  % "slf4j-api"              % slf4jVersion // Akka SLF4J dependency
  val scalaLogging    = "com.typesafe.scala-logging" %% "scala-logging"         % "3.8.0"
  val refined         = "eu.timepit"                 %% "refined-pureconfig"    % refinedVersion
  val pureConfigCore  = "com.github.pureconfig"      %% "pureconfig"            % pureConfigV
  val enumeratum      = "com.github.pureconfig"      %% "pureconfig-enumeratum" % pureConfigV
  val akkaHttpCirce   = "de.heikoseeberger"          %% "akka-http-circe"       % akkaHttpCirceVersion
  val circeGeneric    = "io.circe"                   %% "circe-generic"         % circeVersion
  val circeParser     = "io.circe"                   %% "circe-parser"          % circeVersion
  val kamon           = "io.kamon"                   %% "kamon-akka-http-2.5"   % "1.0.1"
  val kamonSystem     = "io.kamon"                   %% "kamon-system-metrics"  % "1.0.0"
  val scalaTest       = "org.scalatest"              %% "scalatest"             % scalaTestsVersion % "test,it"
  val mockitoCore     = "org.mockito"                % "mockito-core"           % mockitoVersion % "it"
  val sttp            = "com.softwaremill.sttp"      %% "core"                  % sttpVersion
  val sttpJson        = "com.softwaremill.sttp"      %% "json4s"                % sttpVersion
  val pegdown         = "org.pegdown"                % "pegdown"                % "1.6.0" % "test"

  // Server dependencies
  val serverDependencies = Seq(
    akkaActor,
    akkaTestKit,
    akkaStreams,
    cats,
    akkaHttpCore,
    akkaHttp,
    akkaHttpTestkit,
    akkaSlf4j,
    logback,
    slf4j,
    enumeratum,
    refined,
    akkaHttpCirce,
    circeGeneric,
    circeParser,
    kamon,
    kamonSystem,
    scalaLogging,
    scalaTest,
    mockitoCore,
    pegdown
  )

  // service client dependencies
  val clientDependencies = Seq(
    sttp,
    sttpJson,
    slf4j,
    pureConfigCore
  )

  // acceptance tests dependencies
  val acceptanceTestsDependencies = Seq(
    scalaTest,
    pegdown,
    logback
  )
}
