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

case class MLEvalDalek() extends RxDalek[Evaluation] {
  val aml = AmazonMachineLearningClient.builder().withRegion(regions).build()
  
  override def list() = aml.describeEvaluations().getResults()
  override def exterminate(ar: Evaluation) = 
    aml.deleteEvaluation(new DeleteEvaluationRequest().withEvaluationId(ar.getEvaluationId))
  override def describe(ar: Evaluation) = Map(
      ("evaluationId"->ar.getEvaluationId)
  )
  
    override def isSupported() =
    region.isServiceSupported(AmazonMachineLearning.ENDPOINT_PREFIX)

}