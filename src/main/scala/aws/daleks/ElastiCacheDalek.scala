package aws.daleks

import com.amazonaws.regions.Region

import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest
import com.amazonaws.services.elasticache.AmazonElastiCacheClient
import com.amazonaws.services.elasticache.model.CacheCluster
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest
import rx.lang.scala._
import java.util.List
//TODO: Consider pagination
//TODO: Delete cache clusters with replication group
case class ElastiCacheDalek(implicit region: Region) extends RxDalek[CacheCluster] {
  
  val ecache = withRegion(new AmazonElastiCacheClient())

  override def list:List[CacheCluster] = ecache.describeCacheClusters.getCacheClusters

  override def mercy(ar:CacheCluster) = ! "available".equals(ar.getCacheClusterStatus)
  
  override def exterminate(ar:CacheCluster):Unit =
    ecache.deleteCacheCluster(new DeleteCacheClusterRequest()
        .withCacheClusterId(ar.getCacheClusterId))
        
  override def describe(ar:CacheCluster):Map[String,String] = Map(
      ("cacheId"->ar.getCacheClusterId),
      ("status" -> ar.getCacheClusterStatus),
      ("cacheNodes"->ar.getNumCacheNodes.toString()),
      ("replicationGroupId"->ar.getReplicationGroupId))
}