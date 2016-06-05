package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.cloudformation.model.DeleteStackRequest

case class CloudFormationDalek(implicit region: Region) extends Dalek {
  def cfn = new AmazonCloudFormationClient

  def fly = cfn.describeStacks.getStacks.asScala
    .foreach { exterminate(_) }

  def exterminate(stack: Stack): Unit = {
    println(s"${region} | ${stack.getStackName}[${stack.getStackStatus}]")
    exterminate { () =>
      cfn.deleteStack(new DeleteStackRequest()
        .withStackName(stack.getStackName))
    }
  }

}