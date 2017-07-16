package aws.daleks.compute

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.autoscaling.model.AutoScalingGroup
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupRequest
import rx.lang.scala._
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder
import aws.daleks.RxDalek

case class AutoScalingDalek() extends RxDalek[AutoScalingGroup] {
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