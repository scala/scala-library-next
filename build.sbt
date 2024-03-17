ThisBuild / scalaVersion := "2.13.13"

lazy val root = project.in(file("."))
  .aggregate(scalaLibraryNextJVM, scalaLibraryNextJS)
  .settings(
    publish / skip := true,
    // With CrossType.Pure, the root project also picks up the sources in `src`
    Compile / unmanagedSourceDirectories := Nil,
    Test    / unmanagedSourceDirectories := Nil,
  )

lazy val scalaLibraryNext = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .jvmSettings(
    libraryDependencies += "junit" % "junit" % "4.13.2" % Test,
    libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
  )
  .jsEnablePlugins(ScalaJSJUnitPlugin)
  .settings(
    ScalaModulePlugin.scalaModuleSettings,
    scalaModuleAutomaticModuleName := Some("scala.library.next"),
    versionPolicyIntention := Compatibility.None, // TODO Change to `Compatibility.BinaryAndSourceCompatible` after the first release
    scalacOptions ++= Seq("-deprecation", "-feature", "-Werror"),
    libraryDependencies ++= Seq(
      "org.scalacheck" %%% "scalacheck" % "1.17.0" % Test,
    ),
  )
  .jsSettings(
    Test / fork := false,
  )

lazy val scalaLibraryNextJVM = scalaLibraryNext.jvm
lazy val scalaLibraryNextJS  = scalaLibraryNext.js
