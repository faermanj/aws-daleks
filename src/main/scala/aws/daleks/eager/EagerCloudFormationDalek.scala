package aws.daleks.eager

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.DeleteQueueRequest
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.DeleteStackRequest

class EagerCloudFormationDalek(implicit region: Region, credentials: AWSCredentialsProvider) extends Dalek {
  val cloudformation = withRegion(new AmazonCloudFormationClient(credentials), region)

  def exterminate = {
    val stacks = cloudformation.describeStacks.getStacks asScala

    stacks foreach { stack => 
      try {
        println("** Exterminating CloudFormation Stack " + stack.getStackName())
        cloudformation.deleteStack(new DeleteStackRequest().withStackName(stack.getStackName()))
      } catch {
        case e: Exception => println(s"! Failed to exterminate Beanstalk Application ${stack.getStackName}: ${e.getMessage()}")
      }
    }
  }
}