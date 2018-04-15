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
import com.amazonaws.services.ec2.model.KeyPair
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest
import com.amazonaws.services.ec2.model.KeyPairInfo

case class EC2KeysDalek() extends RxDalek[KeyPairInfo] {
  val ec2 = AmazonEC2ClientBuilder.standard().withRegion(regions).build()

  override def list() = ec2.describeKeyPairs().getKeyPairs
  override def exterminate(ar: KeyPairInfo) = ec2.deleteKeyPair(new DeleteKeyPairRequest().withKeyName(ar.getKeyName))
  override def describe(ar: KeyPairInfo) = Map(("keyName" -> ar.getKeyName))

}




