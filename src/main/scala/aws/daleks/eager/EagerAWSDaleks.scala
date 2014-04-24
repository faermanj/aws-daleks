package aws.daleks.eager

import com.amazonaws.regions.Regions
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import com.amazonaws.regions.Region

object EagerAWSDaleks extends App {
  println("EXTERMINATE!")
  implicit val credentials = new ClasspathPropertiesFileCredentialsProvider
  val regions = Regions.values filter { !Regions.GovCloud.equals(_) }

  val regionals = regions
    .map { Region.getRegion(_) }
    .flatMap { implicit region =>
      List(new EagerS3Dalek, 
          new EagerBeanstalkDalek,
          new EagerCloudFormationDalek,
          new EagerDynamoDBDalek,
          new EagerEC2Dalek,
          new EagerElastiCacheDalek,
          new EagerEMRDalek,
          new EagerRDSDalek,
          new EagerS3Dalek,
          new EagerSNSDalek,
          new EagerSQSDalek)      
    }

  val globals = List(
      new EagerRoute53Dalek(),
      new EagerIAMDalek())
      
  val daleks:Seq[Dalek] = regionals ++ globals
  
  daleks.foreach {_.exterminate}
  
  println("EXTERMINATE!")

}

