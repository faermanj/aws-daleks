package aws.daleks.messaging

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.AmazonSQSClient
import aws.daleks.Dalek
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import aws.daleks.RxDalek

case class SQSDalek (implicit region: Region) extends RxDalek[String] {
  val sqs = AmazonSQSClientBuilder.standard().withRegion(regions).build()

  override def list() = sqs.listQueues.getQueueUrls
  override def exterminate(ar: String) =  
    sqs.deleteQueue(ar)
  override def describe(ar: String) = Map(
    "queueURL" -> ar    
  )

 
}