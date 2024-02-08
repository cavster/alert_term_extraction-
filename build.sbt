name := "alert_term_extraction"
 
version := "1.0" 
      
lazy val `alert_term_extraction` = (project in file(".")).enablePlugins(PlayScala)

      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.13.5"

libraryDependencies ++= Seq( jdbc ,
  ehcache,
  ws ,
  specs2 % Test ,
  guice ,
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "com.typesafe.play" %% "play-test" % play.core.PlayVersion.current % Test,// Play Test Utilities

  "com.typesafe.akka" %% "akka-http" % "10.2.10",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,

  "com.typesafe.play" %% "play-json" % "2.9.4")

      