package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.ListTopicsResult
import com.amazonaws.services.sns.model.DeleteTopicRequest
import com.amazonaws.services.sns.model.Topic

case class SNSDalek (implicit region: Region) extends Dalek {
  val sns = withRegion(new AmazonSNSClient)
  
  def fly = fly(sns.listTopics)
  
  def fly(trs:ListTopicsResult) =  {
    //TODO:Paginate
    trs.getTopics.asScala.foreach{exterminate}
  }

  def exterminate(topic:Topic):Unit = {
    val topicArn = topic.getTopicArn
    println(s"${region} | ${topicArn}")
    exterminate { () => 
      sns.deleteTopic(new DeleteTopicRequest().withTopicArn(topicArn))  
    }
  }
}