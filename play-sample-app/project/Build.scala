import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-sample-app"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    javaCore,
    javaJdbc,
    javaEbean,
    "org.webjars" %% "webjars-play" % "2.1.0-2",
    "org.webjars" % "bootstrap" % "2.3.2",
    "com.amazonaws" % "aws-java-sdk" % "1.4.5"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
