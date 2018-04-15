package aws.daleks.networking

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import java.util.List
import java.util.Collections
import aws.daleks.RxDalek
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.NetworkInterface
import com.amazonaws.services.ec2.model.NetworkInterfaceStatus
import com.amazonaws.services.ec2.model.DeleteNetworkInterfaceRequest


case class ENIDalek () extends RxDalek[NetworkInterface] {
  val ec2 = AmazonEC2ClientBuilder.standard().withRegion(regions).build()

  override def list() = ec2.describeNetworkInterfaces.getNetworkInterfaces
    
  override def exterminate(ar: NetworkInterface) = 
    if (ar.getStatus != NetworkInterfaceStatus.InUse)
        ec2.deleteNetworkInterface(new DeleteNetworkInterfaceRequest()
          .withNetworkInterfaceId(ar.getNetworkInterfaceId))
          
  override def describe(ar: NetworkInterface) = Map(
      ("networkInterfaceId"->ar.getNetworkInterfaceId),
      ("status"->ar.getStatus)    
  )   
}