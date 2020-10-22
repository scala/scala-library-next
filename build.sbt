lazy val root = project.in(file("."))
  .settings(
    publish / skip := true
  )

lazy val `scala-library-next` = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
  .in(file("."))
  .jvmSettings(
    libraryDependencies += "junit" % "junit" % "4.13.1" % Test,
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
