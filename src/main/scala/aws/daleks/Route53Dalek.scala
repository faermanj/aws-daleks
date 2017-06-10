package aws.daleks

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import scala.collection.JavaConverters._
import com.amazonaws.services.identitymanagement.model.User
import com.amazonaws.services.identitymanagement.model.DeleteUserRequest
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult
import com.amazonaws.services.identitymanagement.model.DeleteAccessKeyRequest
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata
import com.amazonaws.services.route53.AmazonRoute53
import com.amazonaws.services.route53.AmazonRoute53Client
import com.amazonaws.services.route53.model.HostedZone
import com.amazonaws.services.route53.model.DeleteHostedZoneRequest

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