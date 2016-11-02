organization := "org.reactormonk"
scalaVersion := "2.11.8"
name := "doobie-http4s-argonaut"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-core",
  "org.http4s" %% "http4s-dsl",
  "org.http4s" %% "http4s-argonaut",
  // Replace as needed, http://http4s.org/api/0.14/#package and search "builder"
  "org.http4s" %% "http4s-blaze-server"
).map(_ % "0.14.11a") ++ Seq(
  "org.tpolecat" %% "doobie-core",
  "org.tpolecat" %% "doobie-contrib-specs2"
).map(_ % "0.3.0") ++ Seq(
  "com.github.alexarchambault" %% "argonaut-shapeless_6.1" % "1.1.1",
  "joda-time" % "joda-time" % "2.9.4"
)
