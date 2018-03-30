import Dependencies._
import swaggerboot.playversion.SupportedPlayVersion.Play26

name := "spp-brand-service"

lazy val commonSettings = Seq(
  organization := "de.zalando",
  scalaVersion := "2.12.4",
  credentials ++= Seq(
    Credentials(Path.userHome / ".sbt" / ".bintrayCredentials"),
    Credentials(Path.userHome / ".sbt" / ".nexusCredentials")
  )
)

lazy val strictCompilerSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-explaintypes", // Explain type errors in more detail.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros", // Allow macro definition (besides implementation and application)
    "-language:higherKinds", // Allow higher-kinded types
    "-language:implicitConversions", // Allow definition of implicit functions called views
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Xfuture", // Turn on future language features.
    "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
    "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
    "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
    "-Xlint:option-implicit", // Option.apply used implicit view.
    "-Xlint:package-object-classes", // Class or object defined in package object.
    "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match", // Pattern match may not be typesafe.
    "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification", // Enable partial unification in type constructor inference
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen", // Warn when numerics are widened.
    "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals", // Warn if a local definition is unused.
    "-Ywarn-unused:params", // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates", // Warn if a private member is unused.
    "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
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

lazy val client = (project in file("client"))
  .settings(commonSettings)
  .settings(
    name := "service-client",
    libraryDependencies ++= clientDependencies,
    swaggerSourceDirectory := baseDirectory.value / "../server/src/main/resources/schema",
    swaggerGenerateControllerStubs := false,
    swaggerUpdatePlayRoutes := false,
    swaggerTaggedAttributes := Nil,
    swaggerPlayVersion := Play26,
    swaggerDisableTypesafeIds := true,
    scalaSource in Compile := baseDirectory.value / "src/main/scala"
  )
  .enablePlugins(SwaggerGenerate)

lazy val acceptanceTests = (project in file("acceptance-tests"))
  .settings(commonSettings)
  .settings(
    name := "acceptance-tests",
    description := "BDD-based set of acceptance tests",
    publish := {},
    parallelExecution in Test := false,
    libraryDependencies ++= acceptanceTestsDependencies
  )
  .dependsOn(client)

lazy val server = (project in file("server"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(commonSettings)
  .settings(strictCompilerSettings)
  .settings(dockerSettings)
  .settings(name := "spp-brand-service",
            description := "Main server for the brand service",
            libraryDependencies ++= serverDependencies)
  .enablePlugins(JavaAppPackaging)
