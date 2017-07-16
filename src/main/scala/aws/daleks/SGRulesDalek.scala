package aws.daleks
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.ec2._
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import rx.lang.scala._

case class SGRulesDalek () extends RxDalek[Tuple2[String,IpPermission]]  {
  val ec2 = AmazonEC2ClientBuilder.standard().withRegion(regions).build()
  type Ingress = Tuple2[String,IpPermission]
  
  override def observe() = ec2.describeSecurityGroups.getSecurityGroups.asScala.flatMap{ 
    sg => sg.getIpPermissions.asScala.map{ ip => (sg.getGroupId,ip) }  
  }.toObservable
  
  override def exterminate(ar: Tuple2[String,IpPermission]) = ec2.revokeSecurityGroupIngress(
        new RevokeSecurityGroupIngressRequest()
          .withGroupId(ar._1)
          .withIpPermissions(ar._2))
          
  override def mercy(ar: Ingress) = "default" == ar._1 || EC2.isMercyOnSG(ar._1)
          
  override def describe(ar: Ingress) = Map(
    ("groupId" -> ar._1),
    ("proto" -> ar._2.getIpProtocol),
    ("from" -> ar._2.getFromPort.toString()),
    ("to" -> ar._2.getToPort.toString()),
    ("ipv4" -> ar._2.getIpv4Ranges.toString())
  )
  
}