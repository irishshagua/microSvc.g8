resolvers ++= Seq(
  Resolver.url("sbt-plugins", url("https://dl.bintray.com/fashioninsightscentre/sbt-plugins/"))(Resolver.ivyStylePatterns),
  Resolver.url("zalando-sbt-plugins", url("https://dl.bintray.com/zalando/sbt-plugins/"))(Resolver.ivyStylePatterns),
  Resolver.typesafeRepo("releases"),
  Resolver.bintrayRepo("kamon-io", "sbt-plugins")
)
credentials += Credentials(Path.userHome / ".sbt" / ".bintrayCredentials")

addSbtPlugin("io.kamon"           % "sbt-aspectj-runner"   % "1.1.0")
addSbtPlugin("net.virtual-void"   % "sbt-dependency-graph" % "0.9.0")
addSbtPlugin("com.typesafe.sbt"   % "sbt-native-packager"  % "1.3.3")
addSbtPlugin("de.zalando.buffalo" % "swagger-bootstrapper" % "0.10.0")
