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
import com.amazonaws.services.ec2.model.DescribeSnapshotsRequest

case class EC2SnapshotsDalek() extends RxDalek[Snapshot] {
  val ec2 = AmazonEC2ClientBuilder.standard().withRegion(regions).build()

  override def list() = ec2.describeSnapshots(new DescribeSnapshotsRequest().withOwnerIds("self")).getSnapshots
  override def exterminate(ar: Snapshot) = ec2.deleteSnapshot(new DeleteSnapshotRequest().withSnapshotId(ar.getSnapshotId))
  override def describe(ar: Snapshot) = Map(("snapshotId" -> ar.getSnapshotId))

}

