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

case class MLModelDalek() extends RxDalek[MLModel] {
  val aml = AmazonMachineLearningClient.builder().withRegion(regions).build()
  
  override def list() = aml.describeMLModels().getResults
  
  override def exterminate(ar: MLModel) = {
    //TODO: Ensure after
    aml.deleteRealtimeEndpoint(
        new DeleteRealtimeEndpointRequest().withMLModelId(ar.getMLModelId))
    aml.deleteMLModel(new DeleteMLModelRequest().withMLModelId(ar.getMLModelId))
  }
  
    override def describe(ar: MLModel) = Map(
     ("MLModelId"->ar.getMLModelId)
  )
  
    override def isSupported() =
    region.isServiceSupported(AmazonMachineLearning.ENDPOINT_PREFIX)

}