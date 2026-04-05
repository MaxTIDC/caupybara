scalaVersion := "3.3.7"
name := "caupybara"

mainClass := Some("MainClass.CausalityLTL")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0"
libraryDependencies += "com.lihaoyi" %% "upickle" % "4.4.3"
