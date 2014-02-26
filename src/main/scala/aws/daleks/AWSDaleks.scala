package aws.daleks

import com.amazonaws.regions.Regions
import com.amazonaws.regions.Region
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider

object Main extends App{
    val credentials = new ClasspathPropertiesFileCredentialsProvider
	Regions.values.par foreach {region =>
	  try{
		new EagerRegionDalek(credentials, Region.getRegion(region)).exterminate
	  }catch {
	  	case e:Exception => println(s"! Failed to exterminate region $region: ${e.getMessage} ") 
	  }
	} 
    new EagerGlobalDalek(credentials).exterminate 
}