val Library = new {
  object Version {
    val akka = "2.6.17"
    val akkaHttp = "10.2.7"
    val akkaHttpCirce = "1.38.2"
    val circe = "0.14.1"
    val refined = "0.9.28"
    val scalaTest = "3.0.8"
    val metricsV = "4.2.6"
    val cats = "2.0.0"
    val logback = "1.2.9"
  }

  val logback = "ch.qos.logback" % "logback-classic" % Version.logback

  val akkaStream = "com.typesafe.akka" %% "akka-stream" % Version.akka

  val Prometheus = Seq(
    "io.prometheus" % "simpleclient_common" % "0.14.0",
    "io.prometheus" % "simpleclient_dropwizard" % "0.14.0"
  )

  val Akka = Set(
    "com.typesafe.akka" %% "akka-slf4j",
    "com.typesafe.akka" %% "akka-actor",
    "com.typesafe.akka" %% "akka-stream"
  ).map(_ % Version.akka)

  val akkaHttp = Seq(
      "com.typesafe.akka" %% "akka-http" % Version.akkaHttp,
      "de.heikoseeberger" %% "akka-http-circe" % Version.akkaHttpCirce
    ) ++ Akka

  val akkaHttpTestKit = Seq(
    "com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttp,
    "com.typesafe.akka" %% "akka-stream-testkit" % Version.akka
  ).map(_ % Test)

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
  ).map(_ % Version.circe)

  val refined = "eu.timepit" %% "refined" % Version.refined

  val scalatest = "org.scalatest" %% "scalatest" % Version.scalaTest % "test,provided"

  val jvmMetrics =  Seq(
    "io.dropwizard.metrics" % "metrics-core" % Version.metricsV,
    "io.dropwizard.metrics" % "metrics-jvm" % Version.metricsV,
    "io.dropwizard.metrics" % "metrics-logback" % Version.metricsV
  )

  val cats = Seq(
    "org.typelevel" %% "cats-core" % Version.cats,
    "org.typelevel" %% "cats-kernel" % Version.cats,
    "org.typelevel" %% "cats-macros" % Version.cats
  )

  val brave = Seq(
    "io.zipkin.brave" % "brave" % "5.13.5",
    "io.zipkin.brave" % "brave-instrumentation-http" % "5.13.5",
    "io.zipkin.reporter2" % "zipkin-sender-okhttp3" % "2.16.3"
  )
}

lazy val commonDeps =
  libraryDependencies ++= Library.circe ++ Seq(Library.refined, Library.scalatest) ++ Library.cats :+ Library.logback

lazy val commonConfigs = Seq.empty

lazy val commonSettings = Seq(
  organization := "io.github.uptane",
  organizationName := "uptane",
  organizationHomepage := Some(url("https://uptane.github.io/")),
  licenses += ("MPL-2.0", url("http://mozilla.org/MPL/2.0/")),
  description := "Common  library for uptane scala projects",
  scalaVersion := "2.12.15",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature", "-Ypartial-unification", "-Xexperimental"),
  buildInfoOptions += BuildInfoOption.ToMap,
  buildInfoOptions += BuildInfoOption.BuildTime) ++ Versioning.settings

lazy val sonarSettings = Seq(
  sonarProperties ++= Map(
    "sonar.projectName" -> "OTA Connect LibATS",
    "sonar.projectKey" -> "ota-connect-libats",
    "sonar.host.url" -> "http://sonar.in.here.com",
    "sonar.links.issue" -> "https://saeljira.it.here.com/projects/OTA/issues",
    "sonar.links.scm" -> "https://main.gitlab.in.here.com/olp/edge/ota/connect/back-end/libats-tuf",
    "sonar.links.ci" -> "https://main.gitlab.in.here.com/olp/edge/ota/connect/back-end/libats/pipelines",
    "sonar.language" -> "scala",
    "sonar.projectVersion" -> version.value,
    "sonar.modules" -> "libats,libats-http,libats-http-tracing,libats-slick,libats-messaging-datatype,libats-messaging,libats-metrics,libats-metrics-akka,libats-metrics-prometheus,libats-auth,libats-logging",
    "libats.sonar.projectName" -> "OTA Connect LibATS",
    "libats-http.sonar.projectName" -> "OTA Connect LibATS-HTTP",
    "libats-http-tracing.sonar.projectName" -> "OTA Connect LibATS-HTTP-Tracing",
    "libats-slick.sonar.projectName" -> "OTA Connect LibATS-Slick",
    "libats-messaging-datatype.sonar.projectName" -> "OTA Connect LibATS-Messaging-Datatype",
    "libats-messaging.sonar.projectName" -> "OTA Connect LibATS-Messaging",
    "libats-metrics.sonar.projectName" -> "OTA Connect LibATS-Metrics",
    "libats-metrics-akka.sonar.projectName" -> "OTA Connect LibATS-Metrics-Akka",
    "libats-metrics-prometheus.sonar.projectName" -> "OTA Connect LibATS-Metrics-Prometheus",
    "libats-auth.sonar.projectName" -> "OTA Connect LibATS-Auth",
    "libats-logging.sonar.projectName" -> "OTA Connect LibATS-Logging"
  )
)

lazy val libats = (project in file("libats"))
  .enablePlugins(BuildInfoPlugin, Versioning.Plugin)
  .configs(commonConfigs: _*)
  .settings(commonDeps)
  .settings(commonSettings)
  .settings(Publish.settings)

lazy val libats_http = (project in file("libats-http"))
  .enablePlugins(BuildInfoPlugin, Versioning.Plugin)
  .configs(commonConfigs: _*)
  .settings(commonDeps)
  .settings(commonSettings)
  .settings(libraryDependencies ++= Library.akkaHttp)
  .settings(libraryDependencies ++= Library.jvmMetrics)
  .settings(libraryDependencies ++= Library.circe)
  .settings(libraryDependencies ++= Library.akkaHttpTestKit)
  .settings(Publish.settings)
  .dependsOn(libats)
  .dependsOn(libats_metrics)

lazy val libats_http_tracing = (project in file("libats-http-tracing"))
  .settings(name := "libats-http-tracing")
  .enablePlugins(BuildInfoPlugin, Versioning.Plugin)
  .configs(commonConfigs: _*)
  .settings(commonDeps)
  .settings(commonSettings)
  .dependsOn(libats_http)
  .settings(libraryDependencies ++= Library.brave)
  .settings(Publish.settings)
  .dependsOn(libats)

lazy val libats_slick = (project in file("libats-slick"))
  .enablePlugins(BuildInfoPlugin, Versioning.Plugin)
  .configs(commonConfigs: _*)
  .settings(commonDeps)
  .settings(commonSettings)
  .settings(Publish.settings)
  .settings(libraryDependencies ++= Library.jvmMetrics)
  .settings(libraryDependencies ++= Library.akkaHttpTestKit)
  .dependsOn(libats)
  .dependsOn(libats_http)

lazy val libats_messaging_datatype = (project in file("libats-messaging-datatype"))
  .enablePlugins(BuildInfoPlugin, Versioning.Plugin)
  .configs(commonConfigs: _*)
  .settings(commonSettings)
  .settings(commonDeps)
  .settings(Publish.settings)
  .dependsOn(libats)

lazy val libats_messaging = (project in file("libats-messaging"))
  .enablePlugins(BuildInfoPlugin, Versioning.Plugin)
  .configs(commonConfigs: _*)
  .settings(commonDeps)
  .settings(commonSettings)
  .settings(Publish.settings)
  .settings(libraryDependencies ++= Library.akkaHttpTestKit)
  .dependsOn(libats)
  .dependsOn(libats_metrics)
  .dependsOn(libats_http)
  .dependsOn(libats_messaging_datatype)
  .settings(libraryDependencies ++= Library.Prometheus)

lazy val libats_metrics = (project in file("libats-metrics"))
  .enablePlugins(BuildInfoPlugin, Versioning.Plugin)
  .configs(commonConfigs: _*)
  .settings(commonSettings)
  .settings(libraryDependencies ++= Library.akkaHttp)
  .settings(libraryDependencies ++= Library.circe :+ Library.akkaStream)
  .settings(libraryDependencies ++= Library.jvmMetrics)
  .settings(Publish.settings)

lazy val libats_metrics_akka = (project in file("libats-metrics-akka"))
  .enablePlugins(BuildInfoPlugin, Versioning.Plugin)
  .configs(commonConfigs: _*)
  .settings(commonSettings)
  .settings(Publish.settings)
  .dependsOn(libats_metrics)
  .dependsOn(libats_http)

lazy val libats_metrics_prometheus = (project in file("libats-metrics-prometheus"))
  .enablePlugins(BuildInfoPlugin, Versioning.Plugin)
  .configs(commonConfigs: _*)
  .settings(commonSettings)
  .settings(Publish.settings)
  .settings(libraryDependencies ++= Library.Prometheus)
  .dependsOn(libats_metrics)
  .dependsOn(libats_http)

lazy val libats_logging = (project in file("libats-logging"))
  .enablePlugins(BuildInfoPlugin, Versioning.Plugin)
  .configs(commonConfigs: _*)
  .settings(commonSettings)
  .settings(libraryDependencies ++= Library.circe :+ Library.logback)
  .settings(name := "libats-logging")
  .settings(Publish.settings)

lazy val libats_root = (project in file("."))
  .enablePlugins(DependencyGraph)
  .settings(Publish.disable)
  .settings(scalaVersion := "2.12.15")
  .aggregate(libats, libats_http, libats_http_tracing, libats_messaging, libats_messaging_datatype,
    libats_slick, libats_metrics, libats_metrics_akka,
    libats_metrics_prometheus, libats_logging)
  .settings(sonarSettings)
  .settings(sonarScan / aggregate := false)
