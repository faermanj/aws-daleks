package aws.daleks.compute
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.ec2._
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import aws.daleks.Dalek

case class SGDalek (implicit region: Region) extends Dalek  {
    val ec2 = withRegion(new AmazonEC2Client())

   override def fly = flySGs
     def flySGs = {
    val sgs = ec2.describeSecurityGroups
      .getSecurityGroups
      .asScala
    sgs.foreach { flyRules }
    val sparedSGs:Set[String] = Set() //TODO: Spare SGs of spared instances
    sgs.foreach { exterminate(_, sparedSGs) }
  }

  def exterminate(sg: SecurityGroup, sparedSGs: Set[String]): Unit = {
    val sgId = sg.getGroupId
    val sgName = sg.getGroupName
    val spared = sgName == "default" || sparedSGs.contains(sgId)
    println(s"${region} | ${sgId}")
    exterminate { () =>
      if (!spared)
        ec2.deleteSecurityGroup(
          new DeleteSecurityGroupRequest()
            .withGroupId(sgId))
    }
  }

  def flyRules(sg: SecurityGroup) = {
    sg.getIpPermissions.asScala.foreach { exterminateIngress(sg, _) }
    sg.getIpPermissions.asScala.foreach { exterminateEgress(sg, _) }
  }

  def exterminateEgress(sg: SecurityGroup, perm: IpPermission): Unit = {
    val sgId = sg.getGroupId
    val proto = perm.getIpProtocol
    val from = Option(perm.getFromPort).getOrElse("Any")
    val to = Option(perm.getToPort).getOrElse("Any")
    val src = perm.getIpRanges.asScala.mkString
    println(s"${region} | ${sgId} | ${proto},${from}-${to},${src}")
    exterminate { () =>
      ec2.revokeSecurityGroupEgress(
        new RevokeSecurityGroupEgressRequest()
          .withGroupId(sgId)
          .withIpPermissions(perm))
    }
  }

  def exterminateIngress(sg: SecurityGroup, perm: IpPermission): Unit = {
    val sgId = sg.getGroupId
    val proto = perm.getIpProtocol
    val from = Option(perm.getFromPort).getOrElse("Any")
    val to = Option(perm.getToPort).getOrElse("Any")
    val src = perm.getIpRanges.asScala.mkString
    println(s"${region} | ${sgId} | ${proto},${from}-${to},${src}")
    exterminate { () =>
      ec2.revokeSecurityGroupIngress(
        new RevokeSecurityGroupIngressRequest()
          .withGroupId(sgId)
          .withIpPermissions(perm))
    }
  }

}