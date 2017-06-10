package aws.daleks

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.ec2.model.Reservation
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import scala.util.Try
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.ec2.model.StopInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstanceAttributeRequest
import com.amazonaws.services.ec2.model.InstanceAttributeName
import com.amazonaws.services.ec2.model.InstanceState
import com.amazonaws.services.ec2.model.Volume
import com.amazonaws.services.ec2.model.DeleteVolumeRequest
import com.amazonaws.services.ec2.model.KeyPair
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest
import com.amazonaws.services.ec2.model.KeyPairInfo
import com.amazonaws.services.ec2.model.Image
import com.amazonaws.services.ec2.model.DeregisterImageRequest
import com.amazonaws.services.ec2.model.Snapshot
import com.amazonaws.services.ec2.model.DeleteSnapshotRequest
import com.amazonaws.services.ec2.model.VolumeState
import com.amazonaws.services.ec2.model.DescribeImagesRequest
import com.amazonaws.services.ec2.model.NetworkInterface
import com.amazonaws.services.ec2.model.DeleteNetworkInterfaceRequest
import com.amazonaws.services.ec2.model.NetworkInterfaceStatus
import com.amazonaws.services.ec2.model.SnapshotState
import com.amazonaws.services.ec2.model.DescribeSnapshotsRequest
import com.amazonaws.services.ec2.model.ImageState
import scala.collection.mutable.ListBuffer
import com.amazonaws.services.ec2.model.Address
import com.amazonaws.services.ec2.model.ReleaseAddressRequest
import com.amazonaws.services.ec2.model.SecurityGroup
import com.amazonaws.services.ec2.model.IpPermission
import com.amazonaws.services.ec2.model.RevokeSecurityGroupIngressRequest
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest
import com.amazonaws.services.ec2.model.RevokeSecurityGroupEgressRequest

case class EC2Dalek(implicit region: Region) extends Dalek {
  val RUNNING = 16
  val ec2 = withRegion(new AmazonEC2Client())
  val spared = new ListBuffer[Instance]()

  override def fly = {
    flyInstances
    flyImages
    flyVolumes
    flySnapshots
    flyKeys
    flyENIs
    flyEIPs
  }


  def flyEIPs = ec2.describeAddresses
    .getAddresses
    .asScala
    .foreach { exterminate }

  def flyENIs = ec2.describeNetworkInterfaces
    .getNetworkInterfaces
    .asScala
    .foreach { exterminate }

  def flySnapshots = ec2.describeSnapshots(new DescribeSnapshotsRequest().withOwnerIds("self"))
    .getSnapshots
    .asScala
    .foreach { exterminate }

  def flyImages = ec2
    .describeImages(new DescribeImagesRequest().withOwners("self"))
    .getImages
    .asScala
    .foreach { exterminate }

  def flyKeys = ec2.describeKeyPairs
    .getKeyPairs
    .asScala
    .foreach { exterminate }

  //TODO: Paginate
  def flyVolumes = ec2.describeVolumes
    .getVolumes
    .asScala
    .foreach { exterminate }

  def flyInstances = ec2.describeInstances
    .getReservations
    .asScala
    .flatMap { r => r.getInstances.asScala }
    .foreach { exterminate }

  def exterminate(eip: Address): Unit = {
    val allocId = eip.getAllocationId
    println(s"${region} | ${allocId}")
    exterminate { () =>
      ec2.releaseAddress(new ReleaseAddressRequest()
        .withAllocationId(allocId))
    }
  }

  def exterminate(eni: NetworkInterface): Unit = {
    val eniId = eni.getNetworkInterfaceId
    val eniStatus = NetworkInterfaceStatus.fromValue(eni.getStatus)
    println(s"${region} | ${eniId}[${eniStatus}]")
    exterminate { () =>
      if (eniStatus != NetworkInterfaceStatus.InUse)
        ec2.deleteNetworkInterface(new DeleteNetworkInterfaceRequest()
          .withNetworkInterfaceId(eniId))
    }
  }

  def exterminate(snap: Snapshot): Unit = {
    val snapId = snap.getSnapshotId
    val snapState = SnapshotState.fromValue(snap.getState)
    println(s"${region} | ${snapId}[${snapState}]")
    exterminate { () =>
      ec2.deleteSnapshot(new DeleteSnapshotRequest().withSnapshotId(snapId))
    }
  }

  def exterminate(image: Image): Unit = {
    val imageId = image.getImageId
    val imageState = ImageState.fromValue(image.getState)
    println(s"${region} | ${imageId}[${imageState}]")
    exterminate { () =>
      ec2.deregisterImage(new DeregisterImageRequest().withImageId(imageId))
    }
  }

  def exterminate(kp: KeyPairInfo): Unit = {
    val keyname = kp.getKeyName
    println(s"${region} | ${keyname}")
    exterminate { () =>
      ec2.deleteKeyPair(new DeleteKeyPairRequest().withKeyName(keyname))
    }
  }

  def exterminate(volume: Volume): Unit = {
    val volumeId = volume.getVolumeId
    val volumeState = VolumeState.fromValue(volume.getState)
    println(s"${region} | ${volumeId}[${volumeState}]")
    exterminate { () =>
      if (VolumeState.InUse != volumeState)
        ec2.deleteVolume(
          new DeleteVolumeRequest().withVolumeId(volumeId))
    }
  }

  def exterminate(instance: Instance): Unit = {
    val instanceId = instance.getInstanceId
    val state = instance.getState
    val isRunning = instance.getState == RUNNING
    val isDisableApiTermination = ec2.describeInstanceAttribute(
      new DescribeInstanceAttributeRequest()
        .withInstanceId(instanceId)
        .withAttribute(InstanceAttributeName.DisableApiTermination)).getInstanceAttribute
      .isDisableApiTermination
    println(s"${region} | ${instanceId}[${state.getName}]")
    if (isRunning) exterminate { () =>
      if (isDisableApiTermination) {
        ec2.stopInstances(
          new StopInstancesRequest()
            .withInstanceIds(instanceId))
        spared += instance
      } else
        ec2.terminateInstances(
          new TerminateInstancesRequest()
            .withInstanceIds(instanceId))
    }
  }
}