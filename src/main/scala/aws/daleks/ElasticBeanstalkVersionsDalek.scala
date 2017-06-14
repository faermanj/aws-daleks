package aws.daleks

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import com.amazonaws.services.elasticbeanstalk.model.ApplicationVersionDescription
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationVersionRequest

case class ElasticBeanstalkVersionsDalek(implicit region: Region)  extends RxDalek[ApplicationVersionDescription] {
   val eb = withRegion(new AWSElasticBeanstalkClient)

  override def observe:Observable[ApplicationVersionDescription] = eb.describeApplicationVersions()
    .getApplicationVersions
    .asScala
    .toObservable
    
  override def exterminate(t:ApplicationVersionDescription):Unit = 
    eb.deleteApplicationVersion(
        new DeleteApplicationVersionRequest()
          .withApplicationName(t.getApplicationName)
          .withVersionLabel(t.getVersionLabel))
    
  override def describe(t:ApplicationVersionDescription):Map[String,String] = Map(
      ("applicationVersion"->t.getVersionLabel))
    
}