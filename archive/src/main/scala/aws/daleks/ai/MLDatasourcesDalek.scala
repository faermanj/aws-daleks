package aws.daleks.ai

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import java.util.List
import java.util.Collections
import aws.daleks.RxDalek
import com.amazonaws.services.machinelearning.model._
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient
import com.amazonaws.services.machinelearning.AmazonMachineLearning

case class MLDatasourcesDalek() extends RxDalek[DataSource] {
  val aml = AmazonMachineLearningClient.builder().withRegion(regions).build()
  
  override def list() = aml.describeDataSources().getResults
  
  override def exterminate(ar: DataSource) = 
    aml.deleteDataSource(new DeleteDataSourceRequest().withDataSourceId(ar.getDataSourceId))
  
    override def describe(ar: DataSource) = Map(
      ("dataSourceId"->ar.getDataSourceId)
  )
  
    override def isSupported() =
    region.isServiceSupported(AmazonMachineLearning.ENDPOINT_PREFIX)

}