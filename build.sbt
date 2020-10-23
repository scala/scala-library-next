lazy val root = project.in(file("."))
  .aggregate(`scala-library-next`.jvm, `scala-library-next`.js)
  .settings(
    publish / skip := true,
    // With CrossType.Pure, the root project also picks up the sources in `src`
    Compile / unmanagedSourceDirectories := Nil,
    Test    / unmanagedSourceDirectories := Nil,
  )

lazy val `scala-library-next` = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
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
    scalacOptions ++= Seq("-deprecation", "-feature", "-Werror"),
    libraryDependencies ++= Seq(
      "org.scalacheck" %%% "scalacheck" % "1.14.3" % Test,
    ),
  )
  .jsSettings(
    Test / fork := false,
  )
