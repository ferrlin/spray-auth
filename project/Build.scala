import sbt._
import Keys._


object Build extends Build {
  import Settings._
  import Dependencies._

  /**  */
  lazy val sprayAuth = Project("spray-auth", file("."))
                         .settings(basicSettings: _*)
                         .settings(libraryDependencies ++=
                            compile(akkaActor) ++
                            compile(sprayRouting) ++
                            compile(sprayJson) ++
                            compile(base64) ++
                            test(sprayTestKit) ++
                            test(akkaTestKit) ++
                            test(specs2)
                          )
}


object Settings {
  val VERSION = "0.1-dev"

  lazy val basicSettings = Seq(
    version               := VERSION,
    homepage              := Some(new URL("http://github.com/scalapenos/spray-auth/")),
    organization          := "com.scalapenos",
    organizationHomepage  := Some(new URL("http://scalapenos.com")),
    description           := "A library that provides several authentication- and authorization-related features for Spray-based applications.",
    startYear             := Some(2014),
    licenses              := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalaVersion          := "2.10.4",
    resolvers             ++= Dependencies.resolvers,
    scalacOptions         := Seq(
      "-encoding", "utf8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.7",
      "-Xlog-reflective-calls"
    )
  )
}


object Dependencies {
  val resolvers = Seq(
    "Sonatype Releases"   at "http://oss.sonatype.org/content/repositories/releases",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Spray Repository"    at "http://repo.spray.io/",
    "Base64 Repo"         at "http://dl.bintray.com/content/softprops/maven"
  )

  val akkaVersion  = "2.3.0"
  val sprayVersion = "1.3.1"

  val akkaActor    = "com.typesafe.akka"  %%  "akka-actor"       % akkaVersion
  val akkaSlf4j    = "com.typesafe.akka"  %%  "akka-slf4j"       % akkaVersion
  val akkaTestKit  = "com.typesafe.akka"  %%  "akka-testkit"     % akkaVersion
  val sprayCan     = "io.spray"           %   "spray-can"        % sprayVersion
  val sprayClient  = "io.spray"           %   "spray-client"     % sprayVersion
  val sprayRouting = "io.spray"           %   "spray-routing"    % sprayVersion
  val sprayTestKit = "io.spray"           %   "spray-testkit"    % sprayVersion
  val sprayJson    = "io.spray"           %%  "spray-json"       % "1.2.5"
  val base64       = "me.lessis"          %%  "base64"           % "0.1.0"
  val logback      = "ch.qos.logback"     %   "logback-classic"  % "1.1.1"
  val specs2       = "org.specs2"         %%  "specs2-core"      % "2.3.10"

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
}
