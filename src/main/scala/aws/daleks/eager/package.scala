package aws.daleks

import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.regions.Region
package object eager {
  def withRegion[T <: AmazonWebServiceClient](client: T, region:Region): T = {
    client.setRegion(region)
    client
  }
}