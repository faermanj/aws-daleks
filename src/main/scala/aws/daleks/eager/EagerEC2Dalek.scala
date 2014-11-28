package aws.daleks.eager

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.ec2.model.StopInstancesRequest
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest
import com.amazonaws.services.ec2.model.DeleteVolumeRequest
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest
import com.amazonaws.services.ec2.model.RevokeSecurityGroupIngressRequest
import com.amazonaws.services.ec2.model.RevokeSecurityGroupEgressRequest
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest
import com.amazonaws.services.ec2.model.InstanceState
import com.amazonaws.services.ec2.model.DeregisterImageRequest
import com.amazonaws.services.ec2.model.DescribeImagesRequest

class EagerEC2Dalek(implicit region: Region, credentials: AWSCredentialsProvider) extends Dalek {
  val ec2 = withRegion(new AmazonEC2Client(credentials), region)
  val elb = withRegion(new AmazonElasticLoadBalancingClient(credentials), region)

  def TerminateOrStop(i: Instance) = try {
    println(s"** Exterminating EC2 Instance ${i.getInstanceId} [${i.getState.getName}] on region ${region}")
    ec2.terminateInstances(new TerminateInstancesRequest().withInstanceIds(i.getInstanceId))
  } catch {
    case e: Exception => {
      println("! Failed to terminate EC2 Instance" + i.getInstanceId())
      if ("Running".equalsIgnoreCase(i.getState.getName())) {
        println(s"** Stopping EC2 Instance ${i.getInstanceId} [${i.getState.getName}] on region ${region}")
        ec2.stopInstances(new StopInstancesRequest().withInstanceIds(i.getInstanceId()))
      }
    }
  }

  def exterminateKeypairs = {
    val keypairs = ec2.describeKeyPairs().getKeyPairs().asScala
    keypairs foreach { k =>
      try {
        println("** Exterminating KeyPair " + k.getKeyName())
        ec2.deleteKeyPair(new DeleteKeyPairRequest(k.getKeyName()))
      } catch {
        case e: Exception => println(s"! Failed to exterminate KeyPair ${k.getKeyName()}: ${e.getMessage()}")
      }
    }
  }

  def exterminateInstances = {
    val reservations = ec2.describeInstances.getReservations asScala
    val instances = reservations
      .flatMap { r => r.getInstances asScala }
      .filter { i => !i.getState.getName().equalsIgnoreCase("terminated") }

    instances foreach TerminateOrStop
  }

  //TODO: Recurse dependencies
  def exterminateSecurityGroups = {
    val secGroups = ec2.describeSecurityGroups().getSecurityGroups().asScala.filter(_.getGroupName != "default")
    secGroups foreach { sg =>
      try {

        val ingress = sg.getIpPermissions
        println(s"** Revoking [${ingress.size}] ingress rules for [${sg.getGroupId}]")
        ec2.revokeSecurityGroupIngress(
          new RevokeSecurityGroupIngressRequest()
            .withGroupId(sg.getGroupId())
            .withIpPermissions(ingress))

        val egress = sg.getIpPermissionsEgress
        println(s"** Revoking [${egress.size}] egress rules for [${sg.getGroupId}]")
        ec2.revokeSecurityGroupEgress(
          new RevokeSecurityGroupEgressRequest()
            //.withGroupId(sg.getGroupId())
            .withIpPermissions(egress))

      } catch {
        case e: Exception => println(s"! Failed to clean Security Group ${sg.getGroupId()}: ${e.getMessage()}")
      }
    }

    secGroups.foreach { sg =>
      try {
        println("** Exterminating Security Group " + sg.getGroupId())
        ec2.deleteSecurityGroup(new DeleteSecurityGroupRequest().withGroupId(sg.getGroupId()))
      } catch {
        case e: Exception => println(s"! Failed to exterminate Security Group ${sg.getGroupId()}: ${e.getMessage()}")
      }

    }
  }

  def exterminateVolumes = {
    val volumes = ec2.describeVolumes.getVolumes.asScala.filter {
      v => !"in-use".equals(v.getState)
    }
    volumes filter { "in-use" != _.getState } foreach { v =>
      println(s"** Exterminating Volume ${v.getVolumeId}[${v.getState}]")
      ec2.deleteVolume(new DeleteVolumeRequest().withVolumeId(v.getVolumeId));
    }
  }

  def exterminateELBs = {
    val elbs = elb.describeLoadBalancers().getLoadBalancerDescriptions().asScala
    elbs foreach { lb =>
      try {
        println("** Exterminating Elastic Load Balancer " + lb.getLoadBalancerName())
        elb.deleteLoadBalancer(new DeleteLoadBalancerRequest().withLoadBalancerName(lb.getLoadBalancerName()))
      } catch {
        case e: Exception => println(s"! Failed to exterminate Load Balancer ${lb.getLoadBalancerName()}: ${e.getMessage()}")
      }
    }
  }
  
  def exterminateAMIs = {
    val amis = ec2.describeImages(new DescribeImagesRequest().withOwners("self") ).getImages().asScala
    amis foreach { ami =>
      try{
        println(s"** Exterminating Image [${ami.getImageId}] [${ami.getName}]")
        ec2.deregisterImage(new DeregisterImageRequest().withImageId(ami.getImageId()))
      }catch {
        case e: Exception => println(s"! Failed to exterminate Image [${ami.getImageId()}]: ${e.getMessage()}")
      }
      
    }
  }

  def exterminate = {
    exterminateInstances
    exterminateKeypairs
    exterminateVolumes
    exterminateELBs
    exterminateSecurityGroups
    exterminateAMIs
  }
}
