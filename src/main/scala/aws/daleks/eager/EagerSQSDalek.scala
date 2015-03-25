package aws.daleks.eager

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.DeleteQueueRequest
import aws.daleks.util.Humid

class EagerSQSDalek(implicit region: Region, credentials: AWSCredentialsProvider) extends Dalek {
  val sqs = withRegion(new AmazonSQSClient(credentials), region)

  def exterminate = {
    val queues = sqs.listQueues.getQueueUrls asScala

    queues foreach { q =>
      println("Esterminating SQS Queue " + q)
      Humid {
        sqs.deleteQueue(new DeleteQueueRequest().withQueueUrl(q))
      }
    }
  }
}