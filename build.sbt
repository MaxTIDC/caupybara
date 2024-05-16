ThisBuild / version := "0.1.1"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "fyp-causality"
  )

mainClass := Some("MainClass.CausalityLTL")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0"
libraryDependencies += "com.lihaoyi" %% "upickle" % "3.3.0"
