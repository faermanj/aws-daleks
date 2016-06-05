package aws.daleks

import com.amazonaws.regions.Regions
import com.amazonaws.regions.Regions._
import com.amazonaws.regions.Region
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain

case class AccountDalek() extends Dalek {
  val excludedRegions = List(GovCloud,CN_NORTH_1)
  val regionss = Regions.values diff excludedRegions
  def fly = {
    flyRegions
    flyGlobal
  }

  def flyRegions = 
    regionss.map {Region.getRegion(_)}
            .map {implicit region => RegionDalek()}
            .foreach { _.fly }
            
  def flyGlobal = List(IAMDalek()).foreach(_.fly)
}