package aws.daleks.networking

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import java.util.List
import java.util.Collections
import aws.daleks.RxDalek
import com.amazonaws.services.ec2.model.InternetGateway
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.DeleteInternetGatewayRequest

case class IGWDalek() extends RxDalek[InternetGateway] {
  val ec2 = AmazonEC2ClientBuilder.standard().withRegion(regions).build()

  override def list() = ec2.describeInternetGateways().getInternetGateways
  override def exterminate(ar: InternetGateway) = ec2.deleteInternetGateway(
      new DeleteInternetGatewayRequest().withInternetGatewayId(ar.getInternetGatewayId))
  override def describe(ar: InternetGateway) = Map(
    "internetGatewayId" -> ar.getInternetGatewayId,
    "attachments" -> ar.getAttachments.size().toString()
  )
  
  override def mercy(ar: InternetGateway) = ! ar.getAttachments.isEmpty()
}