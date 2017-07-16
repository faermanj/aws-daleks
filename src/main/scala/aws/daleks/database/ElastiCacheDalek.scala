package aws.daleks.database

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.elasticache.AmazonElastiCacheClient
import com.amazonaws.services.elasticache.model.CacheCluster
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest
import rx.lang.scala._
import java.util.List
import aws.daleks.RxDalek
import com.amazonaws.services.elasticache.AmazonElastiCacheClientBuilder


//TODO: Consider pagination
//TODO: Delete cache clusters with replication group
case class ElastiCacheDalek() extends RxDalek[CacheCluster] {
  
  val ecache = AmazonElastiCacheClientBuilder.standard().withRegion(regions).build()

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