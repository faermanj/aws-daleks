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

case class ElasticBeanstalkDalek(implicit region: Region) extends Dalek {
  val eb = withRegion(new AWSElasticBeanstalkClient)

  def fly = {
    flyEnvs
    flyApps
  }

  def flyEnvs = eb.describeEnvironments
    .getEnvironments
    .asScala
    .foreach { exterminate(_) }
  
  def flyApps = eb.describeApplications
    .getApplications
    .asScala
    .foreach { 
      flyVers(_) 
      exterminate(_)
    } 
  
  def flyVers(app: ApplicationDescription) = eb.describeApplicationVersions()
    .getApplicationVersions
    .asScala
    .foreach { exterminate(_) }

  def exterminate(app: ApplicationDescription): Unit = {

    val appName = app.getApplicationName
    println(s"${region} | ${appName}")
    exterminate { () =>
      eb.deleteApplication(
        new DeleteApplicationRequest()
          .withApplicationName(appName))
    }
  }

  def exterminate(env: EnvironmentDescription): Unit = {
    val envId = env.getEnvironmentId
    val envStatus = env.getStatus
    println(s"${region} | ${envId}[${envStatus}]")
    exterminate { () =>
      if("Terminated" != envStatus)
        eb.terminateEnvironment(new TerminateEnvironmentRequest().withEnvironmentId(envId))
    }
  }

  def exterminate(ver:ApplicationVersionDescription): Unit = {
    val label = ver.getVersionLabel
    println(s"${region} | ${label}")
    exterminate { () =>
      eb.deleteApplicationVersion(new DeleteApplicationVersionRequest().withVersionLabel(label))
    }
  }
}