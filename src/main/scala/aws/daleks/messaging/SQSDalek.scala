package aws.daleks.messaging

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.AmazonSQSClient
import aws.daleks.Dalek

case class SQSDalek (implicit region: Region) extends Dalek {
  val sqs = withRegion(new AmazonSQSClient)
  
  override def fly = sqs.listQueues.getQueueUrls.asScala.foreach{exterminate}
  
  def exterminate(qurl:String):Unit = {
    println(s"${region} | ${qurl}")
    exterminate { () => 
      sqs.deleteQueue(qurl)  
    }
  }
}