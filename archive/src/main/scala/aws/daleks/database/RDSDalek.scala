package aws.daleks.database

import com.amazonaws.regions.Region
import com.amazonaws.services.rds.AmazonRDSClient
import scala.collection.JavaConverters._
import com.amazonaws.services.rds.model.DBInstance
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest
import rx.lang.scala._
import aws.daleks.RxDalek
import com.amazonaws.services.rds.AmazonRDSClientBuilder

case class RDSDalek() extends RxDalek[DBInstance] {
  val rds = AmazonRDSClientBuilder.standard().withRegion(regions).build()
  
  override def observe:Observable[DBInstance] = rds.describeDBInstances
    .getDBInstances
    .asScala
    .toObservable
    
  override def exterminate(ar:DBInstance):Unit = rds.deleteDBInstance(new DeleteDBInstanceRequest()
        .withDBInstanceIdentifier(ar.getDBInstanceIdentifier)
        .withSkipFinalSnapshot(true))
        
  override def describe(ar:DBInstance):Map[String,String] = Map(
    ("dbInstanceId"->ar.getDBInstanceIdentifier)    
  )
}