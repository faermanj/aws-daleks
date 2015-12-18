scalaVersion := "2.11.7"

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.10.40"
libraryDependencies += "io.reactivex" %% "rxscala" % "0.25.0"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.13"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.13"

mainClass := Some("aws.daleks.SupremeDalek")
