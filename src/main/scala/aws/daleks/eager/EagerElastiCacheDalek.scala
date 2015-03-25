package aws.daleks.eager

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.DeleteQueueRequest
import com.amazonaws.services.elasticache.AmazonElastiCacheClient
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest
import aws.daleks.util.Humid

class EagerElastiCacheDalek(implicit region: Region, credentials: AWSCredentialsProvider) extends Dalek {
  val ecache = withRegion(new AmazonElastiCacheClient(credentials), region)

  def exterminate = {
    val caches = ecache.describeCacheClusters.getCacheClusters asScala

    caches foreach { c =>
      try {
        info(this,"Exterminating Cache Cluster " + c.getCacheClusterId)
        Humid{
          ecache.deleteCacheCluster(new DeleteCacheClusterRequest().withCacheClusterId(c.getCacheClusterId()))
        }
      } catch {
        case e: Exception => println(s"! Failed to exterminate Cache Cluster ${c.getCacheClusterId()}: ${e.getMessage()}")
      }
    }
  }
}