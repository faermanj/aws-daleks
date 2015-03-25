package aws.daleks.eager

import java.util.logging.Logger
import aws.daleks.util.Humid

trait Dalek {
  val logger = Logger.getLogger(classOf[Dalek].getName)
  def humidity = if (Humid.isDry) "[DRY]" else "[WET]"
	def exterminate  
  def info(dalek:Dalek,msg:String) = logger.info(s"[$humidity] [$msg]")
}