package aws.daleks.eager

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.DeleteQueueRequest
import com.amazonaws.services.sns.AmazonSNSClient
import scala.collection.JavaConverters._

class EagerSNSDalek(implicit region: Region, credentials: AWSCredentialsProvider) extends Dalek {
  val sns = withRegion(new AmazonSNSClient(credentials),region)

  def exterminate = {
    val topics = sns.listTopics.getTopics asScala 
    
    topics.foreach { t =>
      println("** Exterminating SNS Topic " + t.getTopicArn())
      sns.deleteTopic(t.getTopicArn())
    }
  }
}