import Dependencies._

name := "spp-brand-service"

lazy val commonSettings = Seq(
  organization := "de.zalando",
  scalaVersion := "2.12.5",
  credentials ++= Seq(
    Credentials(Path.userHome / ".sbt" / ".bintrayCredentials"),
    Credentials(Path.userHome / ".sbt" / ".nexusCredentials")
  ),
  testOptions in Test ++= Seq(
    Tests.Argument(TestFrameworks.ScalaTest, "-o"),
    Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports")
  )
)

lazy val dockerSettings = Seq(
  dockerBaseImage := "registry.opensource.zalan.do/stups/openjdk:1.8.0-131-4",
  maintainer := "team-zissou@zalando.ie",
  daemonUser := "root",
  dockerExposedPorts := Seq(9000),
  dockerExposedVolumes := Seq("/opt/docker/logs"),
  dockerRepository := Some("pierone.stups.zalan.do/zissou"),
  mappings in Universal += file("server/src/main/resources/schema/swagger.yaml") -> "zalando-apis/swagger.yaml"
)

lazy val client = (project in file("clients"))
  .settings(commonSettings)
  .settings(
    name := "service-client",
    libraryDependencies ++= clientDependencies
  )

lazy val acceptanceTests = (project in file("acceptance-tests"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(commonSettings)
  .settings(
    name := "acceptance-tests",
    description := "BDD-based set of acceptance tests",
    parallelExecution in Test := false,
    libraryDependencies ++= acceptanceTestsDependencies
  )
  .dependsOn(client)

lazy val server = (project in file("server"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(commonSettings)
  .settings(dockerSettings)
  .settings(name := "spp-brand-service",
            description := "Main server for the brand service",
            libraryDependencies ++= serverDependencies)
  .enablePlugins(JavaAppPackaging)
