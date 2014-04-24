package aws.daleks.eager

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.route53.AmazonRoute53Client
import scala.collection.JavaConverters._

class EagerRoute53Dalek(implicit credentials: AWSCredentialsProvider) extends Dalek {
  val r53 = new AmazonRoute53Client(credentials)
  
  def zones = r53.listHostedZones.getHostedZones.asScala
  
  def exterminate = {
    println("Exterminating Hosted Zones")
    zones.foreach { z =>
      try {
        println("** Exterminating HostedZone " + z.getName)
        // val records = r53.listResourceRecordSets(new ListResourceRecordSetsRequest().withHostedZoneId(z.getId())).getResourceRecordSets() asScala
        // records.foreach
        // TODO
      } catch {
        case e: Exception => println(s"! Failed to exterminate Zone ${z.getName()}: ${e.getMessage()}")
      }
    }

  }
}