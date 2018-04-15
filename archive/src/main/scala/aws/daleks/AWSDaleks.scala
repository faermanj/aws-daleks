package aws.daleks

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import scala.collection.JavaConverters._
import aws.daleks.security.IAM
import scala.util._

object AWSDaleks {

  def main(args: Array[String]): Unit =
    IAM.ping
      .map { (_) => mkDalek(args) } 
      match {
        case Success(dalek) => dalek.fly()
        case Failure(ex) => Console.err.println("Could not access AWS, check your connectivity and credentials.")
      }

  def mkDalek(args: Array[String]) = {
    Daleks.good = !(args.length == 1 && "exterminate" == args(0))
    new AccountDalek()
  }

}