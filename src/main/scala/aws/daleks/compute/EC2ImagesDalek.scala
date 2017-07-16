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

case class EC2ImagesDalek() extends RxDalek[Image] {
  val ec2 = AmazonEC2ClientBuilder.standard().withRegion(regions).build()
  
  override def list() = ec2.describeImages(new DescribeImagesRequest().withOwners("self")).getImages
  override def exterminate(ar: Image) = ec2.deregisterImage(new DeregisterImageRequest().withImageId(ar.getImageId))
  override def describe(ar: Image) = Map(("imageId"->ar.getImageId))

}


