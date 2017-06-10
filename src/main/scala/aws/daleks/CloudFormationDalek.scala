package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.cloudformation.model.DeleteStackRequest
import rx.lang.scala._

//TODO: Consider pagination
case class CloudFormationDalek(implicit region: Region) extends RxDalek[Stack] {
  def cfn = withRegion(new AmazonCloudFormationClient)

  override def observe: Observable[Stack] = cfn.describeStacks.getStacks.asScala.toObservable
  override def exterminate(stack: Stack): Unit =
    cfn.deleteStack(new DeleteStackRequest()
      .withStackName(stack.getStackName))
      
  override def describe(stack: Stack): Map[String, String] = Map(("stackId"->stack.getStackId))


}