package aws.daleks.eager

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentStatus
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationRequest
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription
import com.amazonaws.services.elasticbeanstalk.model.TerminateEnvironmentRequest
import aws.daleks.util.Humid

class EagerBeanstalkDalek(implicit region: Region, credentials: AWSCredentialsProvider) extends Dalek {
  val beanstalk = withRegion(new AWSElasticBeanstalkClient(credentials), region)

  def exterminate = {
    val TERMINATED = EnvironmentStatus.Terminated.toString()
    val envs = beanstalk.describeEnvironments().getEnvironments().asScala filter { e =>
      !TERMINATED.equalsIgnoreCase(e.getStatus())
    }

    val apps = try {
      beanstalk.describeApplications.getApplications asScala
    } catch {
      case e: Exception => {
        println("Could not fectch beanstalk applications: " + e.getMessage());
        List.empty
      }
    }
    
    envs foreach exterminateEnv

    apps foreach exterminateApp
  }

  def exterminateEnv(env: EnvironmentDescription) =
    try {
      val envName = env.getEnvironmentName()
      println(s"** Exterminating Beanstalk Environment ${envName} [${env.getStatus()} ] ")
      Humid {
      beanstalk.terminateEnvironment(new TerminateEnvironmentRequest()
        .withEnvironmentName(envName)
        .withTerminateResources(true))
      }
    } catch {
      case e: Exception => println(s"! Failed to exterminate Beanstalk Environment ${env.getEnvironmentName()} [id: ${env.getEnvironmentId} ]: ${e.getMessage()}");
    }

  def exterminateApp(app: ApplicationDescription) =
    try {
      println("** Exterminating Beanstalk Application " + app.getApplicationName())
      Humid {
      beanstalk.deleteApplication(new DeleteApplicationRequest().withApplicationName(app.getApplicationName()))
      }
    } catch {
      case e: Exception => println(s"! Failed to exterminate Beanstalk Application ${app.getApplicationName()}: ${e.getMessage()}")
    }
}