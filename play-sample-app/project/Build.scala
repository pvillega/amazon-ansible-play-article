import sbt._
import Keys._
import play.Project._
import com.github.play2war.plugin._

object ApplicationBuild extends Build {

  val appName         = "play-sample-app"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    javaCore,
    javaJdbc,
    javaEbean,
    "org.webjars" %% "webjars-play" % "2.1.0-2",
    "org.webjars" % "bootstrap" % "2.3.2",
    "com.amazonaws" % "aws-java-sdk" % "1.4.5",
    "com.feth" %% "play-authenticate" % "0.2.5-SNAPSHOT",
    "be.objectify"  %%  "deadbolt-java"     % "2.1-SNAPSHOT",
    "mysql" % "mysql-connector-java" % "5.1.25"
  )

  val main = play.Project(appName, appVersion, appDependencies)
    .settings(Play2WarPlugin.play2WarSettings: _*)
    .settings(
    resolvers += Resolver.url("Objectify Play Repository (release)", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("Objectify Play Repository (snapshot)", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns),

    Play2WarKeys.servletVersion := "3.0"
  )

}
