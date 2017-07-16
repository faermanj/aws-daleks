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
import com.amazonaws.services.ec2.model.Volume
import com.amazonaws.services.ec2.model.DeleteVolumeRequest
import com.amazonaws.services.ec2.model.Snapshot
import com.amazonaws.services.ec2.model.DeleteSnapshotRequest
case class EC2VolumesDalek() extends RxDalek[Volume] {
  val ec2 = AmazonEC2ClientBuilder.standard().withRegion(regions).build()

  override def list() = ec2.describeVolumes().getVolumes
  override def exterminate(ar: Volume) =  
    ec2.deleteVolume(new DeleteVolumeRequest().withVolumeId(ar.getVolumeId))
    
  override def describe(ar: Volume) = Map((
       "volumeId" -> ar.getVolumeId),
       ("state"->ar.getState))
  
       override def mercy(ar: Volume) = "available" != ar.getState
}