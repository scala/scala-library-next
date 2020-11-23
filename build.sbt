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
    libraryDependencies += "junit" % "junit" % "4.13.1" % Test,
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test,
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
  )
  .jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin))
  .settings(
    ScalaModulePlugin.scalaModuleSettings,
    scalaModuleMimaPreviousVersion := None,
    scalacOptions ++= Seq(
      "-feature",
      "-Werror",
      "-Xlint",
    ),
    libraryDependencies ++= Seq(
      "org.scalacheck" %%% "scalacheck" % "1.15.1" % Test,
    ),
  )
  .jsSettings(
    Test / fork := false,
  )

lazy val scalaLibraryNextJVM = scalaLibraryNext.jvm
lazy val scalaLibraryNextJS  = scalaLibraryNext.js
