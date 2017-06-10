package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.ec2._
case class VPCDalek(implicit region: Region) extends Dalek {
  val ec2 = withRegion(new AmazonEC2Client())

  override def fly = flyVPCs

  def flyVPCs = {
    val vpcs = ec2.describeVpcs().getVpcs.asScala
    vpcs.filter(!_.isDefault)
      .foreach(exterminate)
  }

  def exterminateSubnets(vpc: Vpc, filter:Filter): Unit = {
    def vpcId = vpc.getVpcId
    def subnets = ec2.describeSubnets(new DescribeSubnetsRequest()
      .withFilters(filter))
      .getSubnets
      .asScala
      .foreach(s => exterminateSubnet(s, vpcId))
  }

  def exterminateSubnet(subnet: Subnet, vpcId: String): Unit = {
    val subnetId = subnet.getSubnetId
    println(s"${region} | ${vpcId} | ${subnetId}")
    exterminate {() =>  
      ec2.deleteSubnet(new DeleteSubnetRequest().withSubnetId(subnetId))
    }
  }
  
  def exterminateIGWs(vpc: Vpc,filter:Filter): Unit = 
     ec2.describeInternetGateways(new DescribeInternetGatewaysRequest().withFilters(filter))
                  .getInternetGateways
                  .asScala
                  .foreach(exterminateIGW(_,vpc))
  
  
  def exterminateIGW(igw:InternetGateway,vpc:Vpc):Unit = {
    val igwId = igw.getInternetGatewayId
    println(s"${region} | ${vpc.getVpcId} | ${igwId}")
    exterminate {() =>  
      ec2.deleteInternetGateway(new DeleteInternetGatewayRequest().withInternetGatewayId(igwId))
    }
  }


  def exterminate(vpc: Vpc): Unit = {
    val vpcId = vpc.getVpcId
    //TODO: Reduce API calls by flying all dependencies at once filtering default vpc
    val onlyThisVpc:Filter = new Filter("attachment.vpc-id", List(vpcId).asJava)
    exterminateSubnets(vpc,onlyThisVpc)
    exterminateIGWs(vpc,onlyThisVpc)
    println(s"${region} | ${vpcId}")
    exterminate { () =>
      ec2.deleteVpc(new DeleteVpcRequest().withVpcId(vpc.getVpcId))
    }
  }

}