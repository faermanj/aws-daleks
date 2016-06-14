package aws.daleks

import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest

//TODO: Consider pagination
case class DynamoDBDalek (implicit region: Region) extends Dalek {
  val ddb = withRegion(new AmazonDynamoDBClient)
  
  def fly = ddb.listTables.getTableNames.asScala
               .foreach{exterminate(_)}
  
  def exterminate(tblName:String):Unit = {
    println(s"${region} | ${tblName}")
    exterminate { () => 
      ddb.deleteTable(new DeleteTableRequest().withTableName(tblName))  
    }
  }
}