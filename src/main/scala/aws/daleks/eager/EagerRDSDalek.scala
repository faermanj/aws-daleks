package aws.daleks.eager

import com.amazonaws.services.rds.AmazonRDSClient
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest
import aws.daleks.util.Humid

class EagerRDSDalek(implicit region: Region, credentials: AWSCredentialsProvider) extends Dalek {
  val rds = withRegion(new AmazonRDSClient(credentials), region)

  def exterminate = {
    val databases = rds.describeDBInstances.getDBInstances asScala

    databases foreach { db =>
      println("** Exterminating RDS Database " + db.getDBInstanceIdentifier)
      val delReq = new DeleteDBInstanceRequest
      delReq.setDBInstanceIdentifier(db.getDBInstanceIdentifier())
      delReq.setSkipFinalSnapshot(true);
      Humid {
        rds.deleteDBInstance(delReq)
      }
    }
  }
}