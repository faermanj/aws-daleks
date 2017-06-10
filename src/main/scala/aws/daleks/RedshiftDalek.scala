package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.services.redshift._
import com.amazonaws.services.redshift.model._
import scala.collection.JavaConverters._


case class RedshiftDalek (implicit region: Region) extends Dalek {
  val redshift = withRegion(new AmazonRedshiftClient)
  
 override  def fly = {
    val clusters = redshift
      .describeClusters
      .getClusters
      .asScala
      .map(exterminate)
  }
  
  def exterminate(cluster:Cluster):Unit = {
     val clusterId = cluster.getClusterIdentifier
     println(s"${region} | ${clusterId}")
     exterminate { () =>
       redshift.deleteCluster(new DeleteClusterRequest()
       .withSkipFinalClusterSnapshot(true)
       .withClusterIdentifier(clusterId))
    }  
 }
  
}