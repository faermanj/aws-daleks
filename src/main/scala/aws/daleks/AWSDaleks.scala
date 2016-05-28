package aws.daleks


import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import scala.collection.JavaConverters._

object AWSDaleks {
  def main(args: Array[String]): Unit = {
    Dalek.good = ! (args.length == 1 && 
                 "exterminate" == args(0)) 
    AccountDalek().fly
  }
  
}