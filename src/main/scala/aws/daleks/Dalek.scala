package aws.daleks

import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.regions.Region

trait Dalek {
  

  def withRegion[T <: AmazonWebServiceClient](client: T)(implicit region: Region): T = {
    client.setRegion(region)
    client
  }

  def fly

  def exterminate(f: () => _): Unit =
    if (!Dalek.good) f()
}

object Dalek {
  var good = true
}
