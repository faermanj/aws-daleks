package aws.daleks.analytics

import com.amazonaws.regions.Region
import com.amazonaws.services.redshift._
import com.amazonaws.services.redshift.model._
import scala.collection.JavaConverters._
import aws.daleks.RxDalek


case class RedshiftDalek () extends RxDalek[Cluster] {
  val redshift = AmazonRedshiftClientBuilder.standard().withRegion(regions).build()
  
  override def list = redshift
      .describeClusters
      .getClusters
      
  override def exterminate(ar: Cluster) = redshift.deleteCluster(new DeleteClusterRequest()
       .withSkipFinalClusterSnapshot(true)
       .withClusterIdentifier(ar.getClusterIdentifier))
       
  override def describe(ar: Cluster) = Map(
    ("clusterIdentifier"->ar.getClusterIdentifier)    
  )
  
}