package aws.daleks.compute

import scala.collection.JavaConverters.asScalaBufferConverter

import com.amazonaws.regions.Region
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationRequest

import aws.daleks.RxDalek
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder

case class ElasticBeanstalkDalek() extends RxDalek[ApplicationDescription] {
  val eb =  AWSElasticBeanstalkClientBuilder.standard().withRegion(regions).build()
  
  override def observe:Observable[ApplicationDescription] = eb.describeApplications()
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