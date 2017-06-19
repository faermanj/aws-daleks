package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import scala.collection.JavaConverters._
import com.amazonaws.services.autoscaling.model.AutoScalingGroup
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest
import com.amazonaws.services.autoscaling.model.LaunchConfiguration
import com.amazonaws.services.autoscaling.model.SetDesiredCapacityRequest
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupRequest
import rx.lang.scala._
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder
import com.amazonaws.regions.Regions

case class AutoScalingDalek(implicit region: Region) extends RxDalek[AutoScalingGroup] {
  def as = AmazonAutoScalingClientBuilder.standard().withRegion(regions).build();

  override def observe: Observable[AutoScalingGroup] = as.describeAutoScalingGroups()
    .getAutoScalingGroups
    .asScala
    .toObservable

  override def exterminate(asg: AutoScalingGroup): Unit = {
    as.updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
      .withAutoScalingGroupName(asg.getAutoScalingGroupName)
      .withMinSize(0)
      .withDesiredCapacity(0))
    as.deleteAutoScalingGroup(
      new DeleteAutoScalingGroupRequest()
        .withAutoScalingGroupName(asg.getAutoScalingGroupName))
  }

  override def describe(asg: AutoScalingGroup): Map[String, String] = Map(("autoScalingGroupName" -> asg.getAutoScalingGroupName))

}