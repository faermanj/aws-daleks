package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest
import com.amazonaws.services.elasticache.AmazonElastiCacheClient
import com.amazonaws.services.elasticache.model.CacheCluster
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest

//TODO: Consider pagination
case class ElastiCacheDalek(implicit region: Region) extends Dalek {
  val ecache = withRegion(new AmazonElastiCacheClient())

  val fly = ecache.describeCacheClusters.getCacheClusters.asScala
    .foreach { exterminate(_) }

  def exterminate(cache: CacheCluster): Unit = {
    val cacheId = cache.getCacheClusterId
    println(s"${region} | ${cacheId}")
    exterminate { () =>
      ecache.deleteCacheCluster(new DeleteCacheClusterRequest()
        .withCacheClusterId(cacheId))
    }
  }
}