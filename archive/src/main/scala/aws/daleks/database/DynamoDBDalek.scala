package aws.daleks.database

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest
import rx.lang.scala._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder
import aws.daleks.RxDalek

//TODO: Consider pagination
case class DynamoDBDalek() extends RxDalek[String] {
  val ddb = AmazonDynamoDBAsyncClientBuilder.standard().withRegion(regions).build();
  
  override def observe:Observable[String] = ddb.listTables.getTableNames.asScala.toObservable
  override def exterminate(ar:String):Unit = ddb.deleteTable(new DeleteTableRequest().withTableName(ar))  
  override def describe(ar:String):Map[String,String] = Map(
      ("tableName"->ar)
  )
  
  
  
}