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

case class EC2Dalek(implicit region: Region) extends Dalek {
  val TERMINATED = 48
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
    val state = instance.getState.getCode
    val instanceId = instance.getInstanceId
    println(s"${region} | ${instanceId}")
    if (state != TERMINATED) exterminate { () =>
      Try {
        ec2.terminateInstances(
          new TerminateInstancesRequest()
            .withInstanceIds(instanceId))
      }.recover {
        //TODO: Proper termination protection detection
        case e: AmazonServiceException => ec2.stopInstances(
          new StopInstancesRequest()
            .withInstanceIds(instanceId))
      }

    }
  }

}