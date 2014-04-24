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

  def exterminate = {
    val reservations = ec2.describeInstances.getReservations asScala
    val instances = reservations.flatMap { r => r.getInstances asScala }
    val volumes = ec2.describeVolumes.getVolumes.asScala.filter {
      v => !"in-use".equals(v.getState)
    }

    val keypairs = ec2.describeKeyPairs().getKeyPairs().asScala
    val secGroups = ec2.describeSecurityGroups().getSecurityGroups() asScala
    val elbs = elb.describeLoadBalancers().getLoadBalancerDescriptions() asScala

    instances foreach TerminateOrStop

    volumes filter {"in-use" != _.getState } foreach { v =>
      println(s"** Exterminating Volume ${v.getVolumeId}[${v.getState}]")     
      ec2.deleteVolume(new DeleteVolumeRequest().withVolumeId(v.getVolumeId));
    }

    keypairs foreach { k =>
      try {
        println("** Exterminating KeyPair " + k.getKeyName())
        //ec2.deleteKeyPair(new DeleteKeyPairRequest(k.getKeyName()))
      } catch {
        case e: Exception => println(s"! Failed to exterminate KeyPair ${k.getKeyName()}: ${e.getMessage()}")
      }
    }

    elbs foreach { lb =>
      try {
        println("** Exterminating Elastic Load Balancer " + lb.getLoadBalancerName())
        elb.deleteLoadBalancer(new DeleteLoadBalancerRequest().withLoadBalancerName(lb.getLoadBalancerName()))
      } catch {
        case e: Exception => println(s"! Failed to exterminate Load Balancer ${lb.getLoadBalancerName()}: ${e.getMessage()}")
      }
    }
    
    secGroups foreach { sg =>
      try {
        println("** Exterminating Security Group " + sg.getGroupId())
        ec2.deleteSecurityGroup(new DeleteSecurityGroupRequest().withGroupId(sg.getGroupId()))
      } catch {
        case e: Exception => println(s"! Failed to exterminate Security Group ${sg.getGroupId()}: ${e.getMessage()}")
      }
    }
  }
}
