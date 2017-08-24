enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

name := "Example"

version := "0.1-SNAPSHOT"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}
scalaVersion := "2.12.2"
scalaJSUseMainModuleInitializer := true
//scalaJSModuleKind := ModuleKind.CommonJSModule
mainClass in Compile := Some("example.RunMe")

libraryDependencies ++= Seq()
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2"
libraryDependencies += "io.scalajs" %%% "nodejs" % "0.4.0"
libraryDependencies += "org.typelevel" %%% "cats-core" % "1.0.0-MF"

npmDependencies in Compile += "puppeteer" -> "0.9.0"
npmDependencies in Compile += "bufferutil" -> "^3.0.2"
npmDependencies in Compile += "utf-8-validate" -> "^3.0.3"

npmDevDependencies in Compile := Seq(
  "jshint" -> "^2.9.3",
  "jshint-loader" -> "^0.8.3",
  "babel-core" -> "^6.17.0",
  "babel-loader" -> "^6.2.5",
  "babel-preset-es2017" -> "^6.24.1",
  "concat-with-sourcemaps" -> "1.0.4",
  "source-map-loader" -> "0.1.5")

webpackConfigFile in fastOptJS := Some(baseDirectory.value / "src/main/resources/webpack.js")

version in webpack := "^2.2.0-rc"