package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest
import com.amazonaws.services.dynamodbv2.document.Table
import rx.lang.scala._
//TODO: Consider pagination
case class DynamoDBDalek (implicit region: Region) extends RxDalek[String] {
  val ddb = withRegion(new AmazonDynamoDBClient)
  
  override def observe:Observable[String] = ddb.listTables.getTableNames.asScala.toObservable
  override def exterminate(ar:String):Unit = ddb.deleteTable(new DeleteTableRequest().withTableName(ar))  
  override def describe(ar:String):Map[String,String] = Map(
      ("tableName"->ar)
  )
  
  
  
}