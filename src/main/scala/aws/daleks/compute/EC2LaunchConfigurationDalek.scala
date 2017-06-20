package aws.daleks.compute

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import com.amazonaws.services.autoscaling._
import com.amazonaws.services.autoscaling.model._
import aws.daleks.RxDalek

case class EC2LaunchConfigurationDalek(implicit region: Region)  extends RxDalek[LaunchConfiguration] {
  def as = withRegion(new AmazonAutoScalingClient())
 
  override def observe:Observable[LaunchConfiguration] =  as.describeLaunchConfigurations()
    .getLaunchConfigurations
    .asScala
    .toObservable
  
  override def exterminate(lc:LaunchConfiguration):Unit =
    as.deleteLaunchConfiguration(
        new DeleteLaunchConfigurationRequest()
          .withLaunchConfigurationName(lc.getLaunchConfigurationName))
          
  override def describe(lc:LaunchConfiguration):Map[String,String] = Map(
      ("launchConfigurationName"->lc.getLaunchConfigurationName)
  )
    
}