package aws.daleks.compute

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import java.util.List
import java.util.Collections
import aws.daleks.RxDalek
import com.amazonaws.services.ec2.model.Image
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.DeregisterImageRequest
import com.amazonaws.services.ec2.model.DescribeImagesRequest
import com.amazonaws.services.ec2.model.Address
import com.amazonaws.services.ec2.model.ReleaseAddressRequest

//TODO Do not try to release associated adresses
case class EC2EIPDalek() extends RxDalek[Address] {
  val ec2 = AmazonEC2ClientBuilder.standard().withRegion(regions).build()
  
  override def list() = ec2.describeAddresses
    .getAddresses

  override def exterminate(ar: Address) = {  
    ec2.releaseAddress(new ReleaseAddressRequest().withAllocationId(ar.getAllocationId))
  }
  
  override def describe(ar: Address) = Map(
      ("eipAllocationId"->ar.getAllocationId),
      ("associationId"->ar.getAssociationId)
   )
   
   override def mercy(ar: Address) = ar.getAssociationId != null

}


