package aws.daleks

import com.amazonaws.regions.Region

import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest
import com.amazonaws.services.elasticache.AmazonElastiCacheClient
import com.amazonaws.services.elasticache.model.CacheCluster
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest
import rx.lang.scala._

//TODO: Consider pagination
//TODO: Delete cache clusters with replication group
case class ElastiCacheDalek(implicit region: Region) extends RxDalek[CacheCluster] {
  
  val ecache = withRegion(new AmazonElastiCacheClient())

  override def observe:Observable[CacheCluster] = ecache.describeCacheClusters.getCacheClusters.asScala.toObservable
  override def exterminate(ar:CacheCluster):Unit = 
    if(ar.getReplicationGroupId == null)
      ecache.deleteCacheCluster(new DeleteCacheClusterRequest()
        .withCacheClusterId(ar.getCacheClusterId))

  override def describe(ar:CacheCluster):Map[String,String] = Map(
      ("cacheId"->ar.getCacheClusterId),
      ("cacheNodes"->ar.getNumCacheNodes.toString()),
      ("replicationGroupId"->ar.getReplicationGroupId))
}