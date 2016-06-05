package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest
import com.amazonaws.services.elasticache.AmazonElastiCacheClient
import com.amazonaws.services.elasticache.model.CacheCluster
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient
import com.amazonaws.services.elasticmapreduce.model.ClusterSummary
import com.amazonaws.services.elasticmapreduce.model.TerminateJobFlowsRequest

case class EMRDalek(implicit region: Region) extends Dalek {
  val emr = withRegion(new AmazonElasticMapReduceClient())

  val fly = emr.listClusters().getClusters.asScala.foreach { exterminate }

  def exterminate(cluster: ClusterSummary): Unit = {
    val clusterId = cluster.getId
    println(s"${region} | ${clusterId}")
    exterminate { () =>
      emr.terminateJobFlows(new TerminateJobFlowsRequest()
        .withJobFlowIds(List(clusterId).asJava))
    }
  }
}