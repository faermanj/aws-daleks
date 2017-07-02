package aws.daleks.messaging

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.ListTopicsResult
import com.amazonaws.services.sns.model.DeleteTopicRequest
import com.amazonaws.services.sns.model.Topic
import aws.daleks.Dalek
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import aws.daleks.RxDalek

case class SNSDalek (implicit region: Region) extends RxDalek[Topic] {
  val sns = AmazonSNSClientBuilder.standard().withRegion(regions).build()
  
  override def list() = sns.listTopics.getTopics
  
  override def exterminate(ar: Topic) = sns.deleteTopic(
      new DeleteTopicRequest().withTopicArn(ar.getTopicArn))
  
  override def describe(ar: Topic) = Map(
  ("topicArn"->ar.getTopicArn)    
  )
}