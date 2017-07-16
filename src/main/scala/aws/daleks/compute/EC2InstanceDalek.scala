package aws.daleks.compute

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.ec2._
import aws.daleks.security.IAM
import aws.daleks.RxDalek
import aws.daleks.EC2

case class EC2InstanceDalek() extends RxDalek[Instance] {
  val ec2 = AmazonEC2ClientBuilder.standard().withRegion(regions).build()

  def instances = ec2.describeInstances
    .getReservations
    .asScala
    .flatMap { r => r.getInstances.asScala }

  override def observe: Observable[Instance] = instances.toObservable

  override def mercy(instance: Instance) = {
    val instanceId = instance.getInstanceId
    val isTerminated = instance.getState.getName == "terminated"
    val termProtected = isDisableApiTermination(instanceId)
    val mercy = isTerminated || termProtected
    if (mercy) {
      val ip = instance.getIamInstanceProfile()
      if (ip != null)
        IAM.setMercyOnInstanceProfile(ip)
      instance.getSecurityGroups.asScala.foreach { sg =>
        EC2.setMercyOnSG(sg.getGroupId)
      }
    }
    mercy
  }

  override def exterminate(instance: Instance): Unit = {
    val instanceId = instance.getInstanceId
    ec2.terminateInstances(
      new TerminateInstancesRequest()
        .withInstanceIds(instanceId))

  }

  def isDisableApiTermination(instanceId: String) = ec2.describeInstanceAttribute(
    new DescribeInstanceAttributeRequest()
      .withInstanceId(instanceId)
      .withAttribute(InstanceAttributeName.DisableApiTermination)).getInstanceAttribute
    .isDisableApiTermination

  def getName(i: Instance) = i.getTags
    .asScala
    .find(_.getKey == "Name")
    .map(("instanceName" -> _.getValue))
    .map(Map(_))
    .getOrElse(Map())

  override def describe(i: Instance): Map[String, String] = Map(
    ("instanceId" -> i.getInstanceId),
    ("stateName" -> i.getState.getName),
    ("termProtected" -> isDisableApiTermination(i.getInstanceId).toString)) ++ getName(i)
}