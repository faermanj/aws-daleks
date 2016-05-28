package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import scala.collection.JavaConverters._
import com.amazonaws.services.autoscaling.model.AutoScalingGroup
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest
import com.amazonaws.services.autoscaling.model.LaunchConfiguration

case class AutoScalingDalek(implicit region: Region) extends Dalek {
  def as = withRegion(new AmazonAutoScalingClient())
  def fly = {
    flyASGs
    flyLCs
  }

  def flyASGs = as.describeAutoScalingGroups()
    .getAutoScalingGroups
    .asScala
    .foreach { exterminate(_) }

  def flyLCs = as.describeLaunchConfigurations()
    .getLaunchConfigurations
    .asScala
    .foreach { exterminate(_) }

  def exterminate(asg: AutoScalingGroup): Unit = {
    val asgName = asg.getAutoScalingGroupName
    println(s"${region} | ${asgName}")
    exterminate { () =>
      as.deleteAutoScalingGroup(
        new DeleteAutoScalingGroupRequest()
          .withAutoScalingGroupName(asgName))
    }

  }

  def exterminate(lc: LaunchConfiguration): Unit = {
    val lcName = lc.getLaunchConfigurationName
    println(s"${region} | ${lcName}")
    exterminate { () =>
      as.deleteLaunchConfiguration(
        new DeleteLaunchConfigurationRequest()
          .withLaunchConfigurationName(lcName))
    }

  }

}