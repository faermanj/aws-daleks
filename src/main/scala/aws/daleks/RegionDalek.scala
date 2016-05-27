package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.auth.AWSCredentialsProvider

case class RegionDalek(val region: Region) {
  def exterminate = List(
    S3Dalek(region)).foreach(_.exterminate)

  override def toString = region.toString

  
}