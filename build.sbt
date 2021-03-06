name := "rest-monopoly"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.2"

assemblyJarName in assembly := "vsp_abh928" + ".jar"

organization := "de.vs.rest.server"

seq(webSettings :_*)

mainClass in assembly := Some("JettyMain")

scalacOptions += "-language:postfixOps"

port in container.Configuration := 4567

parallelExecution in Test := false

Keys.test in assembly := {}

libraryDependencies ++= Seq(
  "org.scalatest"           %% "scalatest"                      % "2.2.4",
  "de.alexholly.util"       %% "ip-manager"                     % "0.0.2-SNAPSHOT",
  "de.alexholly.util"       %% "http-client"                    % "0.0.1-SNAPSHOT",
  "de.alexholly.util"       %% "tcp-socket"                     % "0.0.1-SNAPSHOT",
  "org.scalatra"            %% "scalatra"                       % "2.3.0.RC3",
  "org.scalatra"            %% "scalatra-scalate"               % "2.3.0.RC3",
  "org.scalatra"            %% "scalatra-json"                  % "2.3.0.RC3",
  "org.scalatra"            %% "scalatra-atmosphere"            % "2.3.0.RC3",
  "org.json4s"              %% "json4s-jackson"                 % "3.2.10",
  "org.json4s"              %% "json4s-native"                  % "3.2.10",
  "org.json4s"              %% "json4s-ext"                     % "3.2.10",
  "commons-codec"            % "commons-codec"                  % "1.7",
  "org.eclipse.jetty"        % "jetty-server"                   % "9.2.3.v20140905" % "container;compile;test",
  "org.eclipse.jetty"        % "jetty-webapp"                   % "9.2.3.v20140905" % "container;compile;test",
  "org.eclipse.jetty.orbit"  % "javax.servlet"                  % "3.0.0.v201112011016" % "container;compile;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))
)

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/"

ivyXML := <dependencies>
    <exclude module="slf4j-log4j12"/>
    <exclude module="grizzled-slf4j_2.9.1"/>
    <exclude module="jsr311-api" />
  </dependencies>

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
