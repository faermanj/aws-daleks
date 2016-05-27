scalaVersion := "2.11.7"

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.4"
libraryDependencies += "log4j" % "log4j" % "1.2.17"

mainClass := Some("aws.daleks.AWSDaleks")
