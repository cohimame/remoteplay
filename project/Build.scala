import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  import BuildSettings._
  import Dependencies._

  val appName         = "remoteplay"
  val appVersion      = "0.7"

  val appDependencies = Seq(
    jdbc, anorm, akkaRemote, akkaKernel, akkaTestKit
  )

  val myactors = Project(
    id   = "myactors",
    base = file("modules/myactors")
  ).settings(
      scalaVersion := scalaVer,
      description := "core actors for agents and server",
      libraryDependencies ++= Seq(akkaActor, akkaRemote, akkaKernel, akkaTestKit)
    )

  val activeAgent = Project(
    id   = "active-agent",
    base = file("modules/activeagent")
  ).settings(

      scalaVersion := scalaVer,
      description := "active-agent",
      libraryDependencies ++= Seq(akkaActor, akkaRemote, akkaKernel, akkaTestKit)

  ).dependsOn(myactors).aggregate(myactors)

  val passiveAgent = Project(
    id   = "passive-agent",
    base = file("modules/passiveagent")
  ).settings(

      scalaVersion := scalaVer,
      description := "passive-agent",
      libraryDependencies ++= Seq(akkaActor, akkaRemote, akkaKernel, akkaTestKit)

  ).dependsOn(myactors).aggregate(myactors)


  val main = play.Project(appName, appVersion, appDependencies).settings(
    ).dependsOn(myactors).aggregate(myactors)


  object BuildSettings {
    val scalaVer = "2.10.2"
  }

  object Dependencies {
    lazy val version = "2.2.0"
    lazy val akkaActor   =   "com.typesafe.akka" %% "akka-actor"   % version
    lazy val akkaRemote  =   "com.typesafe.akka" %% "akka-remote"  % version
    lazy val akkaKernel  =   "com.typesafe.akka" %% "akka-kernel"  % version
    lazy val akkaTestKit =   "com.typesafe.akka" %% "akka-testkit" % version
  }

}
