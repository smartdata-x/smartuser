name := "choicechecker"

version := "1.0"

scalaVersion := "2.10.4"

resolvers += "Kunyan Repo" at "http://222.73.34.92:8081/nexus/content/groups/public/"

libraryDependencies += "com.kunyan" % "scalautil" % "1.1"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.10.4" % "provided"

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.7.1" % "provided" excludeAll ExclusionRule(organization = "javax.servlet")

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.7.1"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.4"

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.5.2" % "provided"

libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.5" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5" % "test"

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("javax", "el", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

test in assembly := {}
    
