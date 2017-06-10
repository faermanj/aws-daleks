package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.ListTopicsResult
import com.amazonaws.services.sns.model.DeleteTopicRequest
import com.amazonaws.services.sns.model.Topic
import com.amazonaws.services.sqs.AmazonSQSClient

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