package aws.daleks

import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.regions.Region
import com.amazonaws.AmazonServiceException
import scala.util.Try
import rx.lang.scala._

abstract class Dalek {
  type Landing = Map[String,String]

 
  
  def withRegion[T <: AmazonWebServiceClient](client: T)(implicit region: Region): T = {
    client.setRegion(region)
    client
  }

 
  def fly:Unit

  
  def exterminate(f: () => _): Unit =
    if (!Dalek.good) Try {
      f()
    }.recover {
      case e: AmazonServiceException => {
        if (e.getErrorCode == "RequestLimitExceeded") {
          println("X RequestLimitExceeded. Waiting a bit...")
          Thread.sleep(5000)
          exterminate(f)
        } else e.printStackTrace()
      }
      case _ => "Extermination Failed"
    }
}

object Dalek {
  var good = true
}
