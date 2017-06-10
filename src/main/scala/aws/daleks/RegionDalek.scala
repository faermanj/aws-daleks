package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.auth.AWSCredentialsProvider

case class RegionDalek(implicit val  region: Region) {
  def fly = List(
//operations
    CloudFormationDalek(),
//distributed compute
    EMRDalek(),
//compute
    EC2LaunchConfigurationDalek(),
    AutoScalingDalek(),
    EC2InstanceDalek(),    
//data
    S3BucketDalek(),
    RedshiftDalek()
//    ElasticBeanstalkDalek(),   
//    
//    
//    ELBDalek(),
//    
//    RDSDalek(),
//    DynamoDBDalek(),
//    ElastiCacheDalek(),
//    
    

//    
//    EC2Dalek(),
//    VPCDalek(),
//    SGDalek()
  ).foreach(_.fly)

  override def toString = region.toString

  
}