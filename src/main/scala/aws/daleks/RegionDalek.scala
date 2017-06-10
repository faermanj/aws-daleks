package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.auth.AWSCredentialsProvider

case class RegionDalek(implicit val  region: Region) {
  def fly = List(
//compute
//data
      S3BucketDalek()

      
//    CloudFormationDalek(),
//    ElasticBeanstalkDalek(),   
//    
//    AutoScalingDalek(),
//    ELBDalek(),
//    
//    RDSDalek(),
//    DynamoDBDalek(),
//    ElastiCacheDalek(),
//    
//      EMRDalek()
//    RedshiftDalek(),
//    
//    EC2Dalek(),
//    VPCDalek(),
//    SGDalek()
  ).foreach(_.fly)

  override def toString = region.toString

  
}