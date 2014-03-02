package aws.daleks

import com.amazonaws.regions.Regions
import com.amazonaws.regions.Region
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider

object Main extends App {
    println("EXTERMINATE!")
	val credentials = new ClasspathPropertiesFileCredentialsProvider
    val regions = Regions.values filter {! Regions.GovCloud.equals(_)}

	regions.par foreach {(region =>
	  try{
		new EagerRegionDalek(credentials, Region.getRegion(region)).exterminate
	  }catch {
	  	case e:Exception => println(s"! Failed to exterminate region $region: ${e.getMessage} ") 
	  })
	} 
    new EagerGlobalDalek(credentials).exterminate
    println("EXTERMINATE!")
}