package aws.daleks.compute

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient
import com.amazonaws.services.elasticbeanstalk.model.TerminateEnvironmentRequest
import aws.daleks.RxDalek

case class ElasticBeanstalkEnvironmentDalek(implicit region: Region)  extends RxDalek[EnvironmentDescription] {
  val eb = withRegion(new AWSElasticBeanstalkClient)

  override def observe:Observable[EnvironmentDescription] = eb.describeEnvironments
    .getEnvironments
    .asScala
    .toObservable
    
  override def exterminate(env:EnvironmentDescription):Unit = {
    val envId = env.getEnvironmentId
    val envStatus = env.getStatus
    if("Terminated" != envStatus)
      eb.terminateEnvironment(new TerminateEnvironmentRequest().withEnvironmentId(envId))
  }
  
  override def describe(t:EnvironmentDescription):Map[String,String] = Map(("environmentId"->t.getEnvironmentId))
    
}