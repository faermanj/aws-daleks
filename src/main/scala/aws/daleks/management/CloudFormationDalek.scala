package aws.daleks.management

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.cloudformation.model.DeleteStackRequest
import rx.lang.scala._
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder
import aws.daleks.RxDalek

//TODO: Consider pagination
case class CloudFormationDalek() extends RxDalek[Stack] {
  def cfn = AmazonCloudFormationClientBuilder.standard().withRegion(regions).build()

  override def observe: Observable[Stack] = cfn.describeStacks.getStacks.asScala.toObservable
  override def exterminate(stack: Stack): Unit =
    cfn.deleteStack(new DeleteStackRequest()
      .withStackName(stack.getStackName))
      
  override def describe(stack: Stack): Map[String, String] = Map(("stackId"->stack.getStackId))


}