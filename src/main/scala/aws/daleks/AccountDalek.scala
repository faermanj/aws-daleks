package aws.daleks

import com.amazonaws.regions.Regions
import com.amazonaws.regions.Regions._
import com.amazonaws.regions.Region
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest
import scala.collection.JavaConverters._
import scala.util.Try
import aws.daleks.security.IAMRolesDalek
import aws.daleks.security.IAMUserDalek
import aws.daleks.security.IAMInstanceProfilesDalek
import aws.daleks.management.CloudFormationDalek
import aws.daleks.networking.CloudFrontDalek
import aws.daleks.storage.S3BucketDalek

class AccountDalek() extends Dalek[String] {
  val excludedRegions = List(GovCloud,CN_NORTH_1)
  val regionss = Regions.values diff excludedRegions
 
  override def fly() = {
    flyRegions
    flyGlobal
  }

  def flyRegions = 
    regionss.map { Region.getRegion }
            .foreach { region => RegionDalek().fly(region) }
  
  def flyGlobal = List(
      CloudFrontDalek(),
      S3BucketDalek(),
      IAMInstanceProfilesDalek(),
      IAMUserDalek(),
      IAMRolesDalek()
  ).foreach(_.fly)
}