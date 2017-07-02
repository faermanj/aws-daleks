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

case class AccountDalek() {
  val excludedRegions = List(GovCloud,CN_NORTH_1)
  val regionss = Regions.values diff excludedRegions
  
  def fly = {
    flyRegions
    flyGlobal
  }

  def flyRegions = 
    regionss.map {Region.getRegion}
            .map {implicit region => RegionDalek()}
            .foreach { _.fly }
            
  def flyGlobal =  List(CloudFrontDalek()(null)).foreach(_.fly)
  def flyGlobal2 = List(
      IAMInstanceProfilesDalek()(null),
      IAMUserDalek()(null),
      IAMRolesDalek()(null)
  ).foreach(_.fly)
}