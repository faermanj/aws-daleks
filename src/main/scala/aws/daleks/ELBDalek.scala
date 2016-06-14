package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest

case class ELBDalek(implicit region: Region) extends Dalek {
  val elb = withRegion(new AmazonElasticLoadBalancingClient)

  //TODO:Paginate
  def fly = elb.describeLoadBalancers
    .getLoadBalancerDescriptions
    .asScala
    .foreach { exterminate }

  def exterminate(lb: LoadBalancerDescription): Unit = {
    val lbName = lb.getLoadBalancerName
    println(s"${region} | ${lbName}")
    exterminate { () =>
      elb.deleteLoadBalancer(new DeleteLoadBalancerRequest()
        .withLoadBalancerName(lbName))
    }
  }
}