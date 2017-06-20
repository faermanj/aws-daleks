package aws.daleks.messaging

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.ListTopicsResult
import com.amazonaws.services.sns.model.DeleteTopicRequest
import com.amazonaws.services.sns.model.Topic
import aws.daleks.Dalek

case class SNSDalek (implicit region: Region) extends Dalek {
  val sns = withRegion(new AmazonSNSClient)
  
  override def fly = fly(sns.listTopics)
  
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