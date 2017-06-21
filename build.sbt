scalaVersion := "2.11.7"

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.151"
libraryDependencies += "log4j" % "log4j" % "1.2.17"
libraryDependencies += "io.reactivex" %% "rxscala" % "0.26.5"
scalacOptions ++= Seq("-unchecked", "-deprecation")

mainClass := Some("aws.daleks.AWSDaleks")
