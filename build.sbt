organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "com.typesafe.akka" % "akka-remote_2.10" % akkaV,
  // "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "com.sclasen" %% "akka-kafka" % "0.0.7" exclude("javax.jms", "jms") exclude("com.sun.jdmk", "jmxtools") exclude("com.sun.jmx", "jmxri"),
  "org.apache.kafka" %% "kafka" % "0.8.1.1" exclude("javax.jms", "jms") exclude("com.sun.jdmk", "jmxtools") exclude("com.sun.jmx", "jmxri")
  ,"com.typesafe.akka" %% "akka-slf4j" % "2.3.2" % "compile",
  "org.slf4j" % "log4j-over-slf4j" % "1.6.6" % "compile"
  )
}

Revolver.settings
