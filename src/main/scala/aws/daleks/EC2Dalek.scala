package aws.daleks

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.ec2.model.Reservation
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import scala.util.Try
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.ec2.model.StopInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstanceAttributeRequest
import com.amazonaws.services.ec2.model.InstanceAttributeName
import com.amazonaws.services.ec2.model.InstanceState

case class EC2Dalek(implicit region: Region) extends Dalek {
  val RUNNING = 16
  val ec2 = withRegion(new AmazonEC2Client())
  def fly = {
    flyInstances
  }

  def flyInstances = ec2.describeInstances
    .getReservations
    .asScala
    .flatMap { r => r.getInstances.asScala }
    .foreach { exterminate(_) }

  def exterminate(instance: Instance): Unit = {
    val instanceId = instance.getInstanceId
    val state = instance.getState
    val isRunning = instance.getState == RUNNING
    val isDisableApiTermination = ec2.describeInstanceAttribute(
      new DescribeInstanceAttributeRequest()
        .withInstanceId(instanceId)
        .withAttribute(InstanceAttributeName.DisableApiTermination)).getInstanceAttribute
      .isDisableApiTermination
    println(s"${region} | ${instanceId}[${state.getName}]")
    if (isRunning) exterminate { () =>
      if (isDisableApiTermination)
        ec2.stopInstances(
          new StopInstancesRequest()
            .withInstanceIds(instanceId))
      else
        ec2.terminateInstances(
          new TerminateInstancesRequest()
            .withInstanceIds(instanceId))
    }
  }
}