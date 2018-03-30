import sbt._

object Dependencies {

  val akkaVersion           = "2.5.6"
  val akkaHttpVersion       = "10.0.6"
  val catsVersion           = "1.0.0-RC1"
  val slf4jVersion          = "1.7.25"
  val logbackVersion        = "1.2.3"
  val scalaTestsVersion     = "3.0.5"
  val cucumberVersion       = "2.0.0"
  val junitVersion          = "4.12"
  val junitInterfaceVersion = "0.11"
  val playClientVersion     = "1.1.3"
  val playVersion           = "2.6.7"
  val refinedVersion        = "0.8.7"
  val enumeratumVersion     = "0.9.0"
  val akkaHttpCirceVersion  = "1.20.0-RC2"
  val circeVersion          = "0.9.1"
  val mockitoVersion        = "2.8.9"

  // akka
  val akkaActor   = "com.typesafe.akka" %% "akka-actor"   % akkaVersion
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  // streams
  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val cats        = "org.typelevel"     %% "cats-core"   % catsVersion
  // akka http
  val akkaHttpCore    = "com.typesafe.akka" %% "akka-http-core"    % akkaHttpVersion
  val akkaHttp        = "com.typesafe.akka" %% "akka-http"         % akkaHttpVersion
  val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % IntegrationTest
  //logger
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j"     % akkaVersion    // Akka SLF4J
  val logback   = "ch.qos.logback"    % "logback-classic" % logbackVersion // SLF4J backend
  val slf4j     = "org.slf4j"         % "slf4j-api"       % slf4jVersion   // Akka SLF4J dependency

  // cucumber
  val cucumber       = "io.cucumber"  %% "cucumber-scala" % cucumberVersion
  val cucumberJunit  = "io.cucumber"  % "cucumber-junit"  % cucumberVersion
  val junit          = "junit"        % "junit"           % junitVersion
  val junitInterface = "com.novocode" % "junit-interface" % junitInterfaceVersion

  val playWs         = "com.typesafe.play" %% "play-ws"                 % playVersion
  val playClient     = "com.typesafe.play" %% "play-ahc-ws-standalone"  % playClientVersion
  val playClientJson = "com.typesafe.play" %% "play-ws-standalone-json" % playClientVersion

  val awaitility = "org.awaitility" % "awaitility" % "3.0.0"

  val refined       = "eu.timepit"                 %% "refined-pureconfig"    % refinedVersion
  val enumeratum    = "com.github.pureconfig"      %% "pureconfig-enumeratum" % enumeratumVersion
  val akkaHttpCirce = "de.heikoseeberger"          %% "akka-http-circe"       % akkaHttpCirceVersion
  val circeGeneric  = "io.circe"                   %% "circe-generic"         % circeVersion
  val circeParser   = "io.circe"                   %% "circe-parser"          % circeVersion
  val kamon         = "io.kamon"                   %% "kamon-akka-http-2.5"   % "1.0.1"
  val kamonSystem   = "io.kamon"                   %% "kamon-system-metrics"  % "1.0.0"
  val scalaLogging  = "com.typesafe.scala-logging" %% "scala-logging"         % "3.8.0"
  val scalaTest     = "org.scalatest"              %% "scalatest"             % scalaTestsVersion % "test,it"
  val mockitoCore   = "org.mockito"                % "mockito-core"           % mockitoVersion % "it"

  // brand service server dependencies
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
    mockitoCore
  )

  // brand service client dependencies
  val clientDependencies = Seq(
    akkaActor,
    akkaStreams,
    akkaHttp,
    akkaHttpCore,
    akkaSlf4j,
    logback,
    playClient,
    playClientJson,
    playWs
  )

  // acceptance tests dependencies
  val acceptanceTestsDependencies = Seq(
    cucumber,
    cucumberJunit,
    junit,
    junitInterface,
    awaitility
  )
}
