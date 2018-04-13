resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.bintrayRepo("kamon-io", "sbt-plugins")
)

addSbtPlugin("io.kamon"         % "sbt-aspectj-runner"   % "1.1.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager"  % "1.3.3")
addSbtPlugin("com.tapad"        % "sbt-docker-compose"   % "1.0.34")
