package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient
import scala.collection.JavaConverters._
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationRequest
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription
import com.amazonaws.services.elasticbeanstalk.model.TerminateEnvironmentRequest
import com.amazonaws.services.elasticbeanstalk.model.ApplicationVersionDescription
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationVersionRequest
import rx.lang.scala._

case class ElasticBeanstalkDalek(implicit region: Region) extends RxDalek[ApplicationDescription] {
  val eb = withRegion(new AWSElasticBeanstalkClient)
  
  override def observe:Observable[ApplicationDescription] = eb.describeApplications
    .getApplications
    .asScala 
    .toObservable
    
  override def exterminate(app:ApplicationDescription):Unit =   eb.deleteApplication(
        new DeleteApplicationRequest()
          .withApplicationName(app.getApplicationName))
          
  override def describe(t:ApplicationDescription):Map[String,String] = 
    Map("application"->t.getApplicationName)

  override def flyDependencies(t:ApplicationDescription) = {
    List(
        ElasticBeanstalkVersionsDalek(),
        ElasticBeanstalkEnvironmentDalek()).foreach(_.fly)
    
  }
  

}