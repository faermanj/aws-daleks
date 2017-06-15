package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.auth.AWSCredentialsProvider

case class RegionDalek(implicit val  region: Region) {
  def fly = List(
//composite services
    CloudFormationDalek(),
    ElasticBeanstalkDalek(),
//distributed compute
    EMRDalek(),
//compute
    EC2LaunchConfigurationDalek(),
    AutoScalingDalek(),
    ELBDalek(),
    EC2InstanceDalek(),    
//data
    ElastiCacheDalek(),
    S3BucketDalek(),
    RedshiftDalek(),
    RDSDalek(),
    DynamoDBDalek()
//networking   
//    EC2Dalek(),
//    VPCDalek(),
//    SGDalek()
  ).foreach(_.fly)

  override def toString = region.toString

  
}