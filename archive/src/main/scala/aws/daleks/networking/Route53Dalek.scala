package aws.daleks.networking

import scala.collection.JavaConverters._
import com.amazonaws.services.route53.AmazonRoute53Client
import com.amazonaws.services.route53.model.HostedZone
import com.amazonaws.services.route53.model.DeleteHostedZoneRequest
import aws.daleks.RxDalek
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder
import com.amazonaws.regions.Region

case class Route53Dalek() extends RxDalek[HostedZone] {
  val r53 = AmazonRoute53ClientBuilder.defaultClient()

  override def list() = r53.listHostedZones.getHostedZones
  override def exterminate(ar: HostedZone) =
          r53.deleteHostedZone(new DeleteHostedZoneRequest().withId(ar.getId))
          
  override def describe(ar: HostedZone) = Map(
      ("zoneId"->ar.getId)
      
  )

 
}