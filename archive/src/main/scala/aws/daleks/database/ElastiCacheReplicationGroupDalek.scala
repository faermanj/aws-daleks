package aws.daleks.database

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.elasticache.AmazonElastiCacheClient
import rx.lang.scala._
import com.amazonaws.services.elasticache.model.ReplicationGroup
import com.amazonaws.services.elasticache.model.DeleteReplicationGroupRequest
import java.util.List
import aws.daleks.RxDalek
import com.amazonaws.services.elasticache.AmazonElastiCacheClientBuilder

//TODO: Consider pagination
//TODO: Delete cache clusters with replication group
case class ElastiCacheReplicationGroupDalek() extends RxDalek[ReplicationGroup] {
  
  val ecache = AmazonElastiCacheClientBuilder.standard().withRegion(regions).build()

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