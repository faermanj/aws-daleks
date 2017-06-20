package aws.daleks.networking

import scala.collection.JavaConverters._
import com.amazonaws.services.route53.AmazonRoute53Client
import com.amazonaws.services.route53.model.HostedZone
import com.amazonaws.services.route53.model.DeleteHostedZoneRequest
import aws.daleks.Dalek

case class Route53Dalek() extends Dalek {
  val r53 = new AmazonRoute53Client()

  override def fly = flyZones
  

  def flyZones = r53.listHostedZones.getHostedZones
    .asScala
    .foreach { exterminate(_) }

  
  def exterminate(zone: HostedZone): Unit = {
    val zoneId = zone.getId
    println(s"${zoneId}")
    exterminate { () =>
      r53.deleteHostedZone(new DeleteHostedZoneRequest().withId(zoneId))
    }
  }
}