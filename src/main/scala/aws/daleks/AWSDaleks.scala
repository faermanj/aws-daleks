package aws.daleks

import com.amazonaws.regions.Regions
import com.amazonaws.regions.Region

object Main extends App{
	Regions.values.par foreach {region =>
	  try{
		new EagerRegionDalek(Region.getRegion(region)).exterminate
	  }catch {
	  	case e:Exception => println(s"! Could not send Dalek to region $region: ${e.getMessage} ") 
	  }
	} 
}