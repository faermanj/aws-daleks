package aws.daleks

import com.amazonaws.regions.Regions
import com.amazonaws.regions.Regions._
import com.amazonaws.regions.Region
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain

case class AccountDalek() {
  val excludedRegions = List(GovCloud,CN_NORTH_1)
  val regionss = Regions.values diff excludedRegions
  def exterminate = exterminateRegions

  def exterminateRegions = 
    regionss.map {Region.getRegion(_)}
            .map {RegionDalek(_)}
            .foreach { _.exterminate }
            
}