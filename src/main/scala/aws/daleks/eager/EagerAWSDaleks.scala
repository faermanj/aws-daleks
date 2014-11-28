package aws.daleks.eager

import com.amazonaws.regions.Regions
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain

object EagerAWSDaleks extends App {
  println("EXTERMINATE!")
  implicit val credentials = new DefaultAWSCredentialsProviderChain
  val regions = Regions.values filter { !Regions.GovCloud.equals(_) }

  val globals = List(
    new EagerRoute53Dalek(),
    new EagerIAMDalek())

  val regionals = regions
    .map { Region.getRegion(_) }
    .par
    .foreach { implicit region =>
      println("Preparing extermination of region ["+region+"]")
      List(new EagerS3Dalek,
        new EagerBeanstalkDalek,
        new EagerCloudFormationDalek,
        new EagerDynamoDBDalek,        
        new EagerElastiCacheDalek,
        new EagerEMRDalek,
        new EagerRDSDalek,
        new EagerS3Dalek,
        new EagerSNSDalek,
        new EagerSQSDalek,
        new EagerEC2Dalek) foreach {_.exterminate}
    } 

  globals foreach { _.exterminate }

  println("EXTERMINATE!")

}

