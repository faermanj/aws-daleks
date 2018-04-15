package aws.daleks.compute

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import com.amazonaws.services.lambda.model.FunctionConfiguration
import com.amazonaws.services.lambda.model.DeleteFunctionRequest
import com.amazonaws.services.lambda.AWSLambda
import com.amazonaws.services.lambda.AWSLambdaClientBuilder
import aws.daleks.RxDalek

case class LambdaDalek() extends RxDalek[FunctionConfiguration] {
  val lambda = AWSLambdaClientBuilder.standard().withRegion(regions).build

  override def list() = lambda.listFunctions().getFunctions()

  override def mercy(ar: FunctionConfiguration) = isSparedName(ar.getFunctionName)

  override def exterminate(ar: FunctionConfiguration) =
    lambda.deleteFunction(new DeleteFunctionRequest().withFunctionName(ar.getFunctionName))

  override def describe(ar: FunctionConfiguration) = Map(
    ("functionName" -> ar.getFunctionName))

   override def isSupported() =
     region.isServiceSupported(AWSLambda.ENDPOINT_PREFIX)
}