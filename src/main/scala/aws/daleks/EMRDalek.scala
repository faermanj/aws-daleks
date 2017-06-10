package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.elasticache.AmazonElastiCacheClient
import com.amazonaws.services.elasticache.model.CacheCluster
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient
import com.amazonaws.services.elasticmapreduce.model.Cluster
import com.amazonaws.services.elasticmapreduce.model.ClusterSummary
import com.amazonaws.services.elasticmapreduce.model.TerminateJobFlowsRequest
import com.amazonaws.services.elasticmapreduce.model.DescribeClusterRequest
import rx.lang.scala._
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder
import com.amazonaws.AmazonWebServiceClient

case class EMRDalek(implicit region: Region) extends RxDalek[Cluster] {
  val emr = AmazonElasticMapReduceClientBuilder.standard().withRegion(region.getName).build()

  def toCluster(clusterSum: ClusterSummary): Cluster =
    emr.describeCluster(
      new DescribeClusterRequest().withClusterId(clusterSum.getId)).getCluster

  override def describe(cluster: Cluster):Map[String,String] = Map(("clusterId" -> cluster.getId),
    ("state" -> cluster.getStatus.getState),
    ("termProtected" -> cluster.getTerminationProtected.toString))

  def listActiveClusters =
    emr.listClusters.getClusters.asScala.map(toCluster).filter(notTerminated)

  def notTerminated(cluster: Cluster) = ! cluster.getStatus.getState.startsWith("TERM")
    
  override def observe: Observable[Cluster] =
    listActiveClusters.toObservable

  override def exterminate(cluster:Cluster): Unit = {
    val termProtected = cluster.isTerminationProtected()      
    if (! termProtected) emr.terminateJobFlows(
      new TerminateJobFlowsRequest()
        .withJobFlowIds(List(cluster.getId)
          .asJava))
  }
}