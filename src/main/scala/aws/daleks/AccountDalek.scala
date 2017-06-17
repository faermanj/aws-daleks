package aws.daleks

import com.amazonaws.regions.Regions
import com.amazonaws.regions.Regions._
import com.amazonaws.regions.Region
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest
import scala.collection.JavaConverters._
import scala.util.Try

case class AccountDalek() extends Dalek {
  val excludedRegions = List(GovCloud,CN_NORTH_1)
  val regionss = Regions.values diff excludedRegions
  
  override def fly = {
    flyRegions
    flyGlobal
  }

  def flyRegions = 
    regionss.map {Region.getRegion}
            .map {implicit region => RegionDalek()}
            .foreach { _.fly }
            
  def flyGlobal = List(
      IAMInstanceProfilesDalek()(null),
      IAMUserDalek()(null),
      IAMRolesDalek()(null)
  ).foreach(_.fly)
}