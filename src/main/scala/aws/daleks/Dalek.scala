package aws.daleks

import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.regions.Region

trait Dalek {
  def withRegion[T <: AmazonWebServiceClient](client: T, region: Region): T = {
    client.setRegion(region)
    client
  }
  
  def exterminate
}