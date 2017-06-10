package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.services.rds.AmazonRDSClient
import scala.collection.JavaConverters._
import com.amazonaws.services.rds.model.DBInstance
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest

case class RDSDalek(implicit region: Region) extends Dalek {
  val rds = withRegion(new AmazonRDSClient)
  override def fly = rds.describeDBInstances
    .getDBInstances
    .asScala
    .foreach { exterminate(_) }

  def exterminate(dbi: DBInstance): Unit = {
    val dbId = dbi.getDBInstanceIdentifier
    println(s"${region} | ${dbId}")
    exterminate { () =>
      rds.deleteDBInstance(new DeleteDBInstanceRequest()
        .withDBInstanceIdentifier(dbId)
        .withSkipFinalSnapshot(true))
    }
  }
}