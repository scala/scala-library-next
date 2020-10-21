lazy val root = project.in(file("."))
  .settings(ScalaModulePlugin.scalaModuleSettings)
  .settings(
    name    := "scala-library-next",
    scalacOptions ++= Seq("-deprecation", "-feature", "-Werror"),
    scalaModuleMimaPreviousVersion := None,
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.13.1" % Test,
      "org.scalacheck" %% "scalacheck" % "1.14.3" % Test,
    )
  )
