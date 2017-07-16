package aws.daleks.compute

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient
import com.amazonaws.services.elasticbeanstalk.model.TerminateEnvironmentRequest
import aws.daleks.RxDalek
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder

case class ElasticBeanstalkEnvironmentDalek()  extends RxDalek[EnvironmentDescription] {
  val eb = AWSElasticBeanstalkClientBuilder.standard().withRegion(regions).build()

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