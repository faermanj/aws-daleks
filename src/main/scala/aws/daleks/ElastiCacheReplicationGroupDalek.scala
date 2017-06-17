package aws.daleks

import com.amazonaws.regions.Region

import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest
import com.amazonaws.services.elasticache.AmazonElastiCacheClient
import com.amazonaws.services.elasticache.model.CacheCluster
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest
import rx.lang.scala._
import com.amazonaws.services.elasticache.model.ReplicationGroup
import com.amazonaws.services.elasticache.model.DeleteReplicationGroupRequest
import java.util.List
//TODO: Consider pagination
//TODO: Delete cache clusters with replication group
case class ElastiCacheReplicationGroupDalek(implicit region: Region) extends RxDalek[ReplicationGroup] {
  
  val ecache = withRegion(new AmazonElastiCacheClient())

  override def list:List[ReplicationGroup] = 
      ecache.describeReplicationGroups.getReplicationGroups()
  
  override def mercy(ar:ReplicationGroup) = ar.getStatus != "available"
  
  override def exterminate(ar:ReplicationGroup):Unit =     
    ecache.deleteReplicationGroup(new DeleteReplicationGroupRequest().withReplicationGroupId(ar.getReplicationGroupId))
  

  override def describe(ar:ReplicationGroup):Map[String,String] = Map(
      ("replicationGroupId"->ar.getReplicationGroupId()),
      ("status"->ar.getStatus())
  )
}