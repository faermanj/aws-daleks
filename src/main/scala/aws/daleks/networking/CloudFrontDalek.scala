package aws.daleks.networking

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import java.util.List
import java.util.Collections
import com.amazonaws.services.cloudfront.model.Distribution
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder
import aws.daleks.RxDalek
import com.amazonaws.services.cloudfront.model.ListDistributionsResult
import com.amazonaws.services.cloudfront.model.ListDistributionsRequest
import com.amazonaws.services.cloudfront.model.DistributionSummary
import com.amazonaws.services.cloudfront.model.UpdateDistributionRequest
import com.amazonaws.services.cloudfront.model.DistributionConfig
import com.amazonaws.services.cloudfront.model.DeleteDistributionRequest
import com.amazonaws.services.cloudfront.model.GetDistributionConfigRequest

case class CloudFrontDalek() extends RxDalek[DistributionSummary] {

  val cloudfront = AmazonCloudFrontClientBuilder.standard().withRegion("us-east-1").build()

  override def list() = cloudfront.listDistributions(new ListDistributionsRequest()).getDistributionList().getItems
  override def exterminate(ar: DistributionSummary) = {
    val result = cloudfront.getDistributionConfig(
      new GetDistributionConfigRequest().withId(ar.getId))
    val etag = result.getETag()
    //TODO Wait for disablement
    if (ar.isEnabled()) {
      val distributionConfig =
        result.getDistributionConfig
      distributionConfig.setEnabled(false)
      cloudfront.updateDistribution(
        new UpdateDistributionRequest()
          .withIfMatch(etag)
          .withId(ar.getId)
          .withDistributionConfig(distributionConfig))
    } else {
      cloudfront.deleteDistribution(
        new DeleteDistributionRequest()
          .withIfMatch(etag)
          .withId(ar.getId))
    }
  }

  override def describe(ar: DistributionSummary) = Map(
    ("distributionId" -> ar.getId),
    ("enabled" -> ar.isEnabled().toString()))

}