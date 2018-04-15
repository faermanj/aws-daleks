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

case class MLBatchDalek() extends RxDalek[BatchPrediction] {
  val aml = AmazonMachineLearningClient.builder().withRegion(regions).build()
  
  override def list() = aml.describeBatchPredictions().getResults
  
  override def exterminate(ar: BatchPrediction) = 
    aml.deleteBatchPrediction(new DeleteBatchPredictionRequest().withBatchPredictionId(ar.getBatchPredictionId))
  
    override def describe(ar: BatchPrediction) = Map(
      ("batchPredictionId"->ar.getBatchPredictionId)
  )
  
    override def isSupported() =
    region.isServiceSupported(AmazonMachineLearning.ENDPOINT_PREFIX)

}