 package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.auth.AWSCredentialsProvider
import aws.daleks.compute._
import aws.daleks.management.CloudFormationDalek
import aws.daleks.database.ElastiCacheDalek
import aws.daleks.database.DynamoDBDalek
import aws.daleks.compute.ElasticBeanstalkDalek
import aws.daleks.networking.ELBDalek
import aws.daleks.analytics.EMRDalek
import aws.daleks.compute.LambdaDalek
import aws.daleks.database.RDSDalek
import aws.daleks.analytics.RedshiftDalek
import aws.daleks.ai._

case class RegionDalek(implicit val  region: Region) {
  def fly = List(
//composite services
    CloudFormationDalek(),
    ElasticBeanstalkDalek(),
//distributed compute
    EMRDalek(),
//compute
    LambdaDalek(),
    EC2LaunchConfigurationDalek(),
    AutoScalingDalek(),
    ELBDalek(),
    EC2InstanceDalek(),
    EC2ImagesDalek(),
    EC2VolumesDalek(),
    EC2SnapshotsDalek(),
    EC2KeysDalek(),
//AI
    MLEvalDalek(),
    MLBatchDalek(),
    MLModelDalek(),
    MLDatasourcesDalek(),
//data
    ElastiCacheDalek(),
    S3BucketDalek(),
    RedshiftDalek(),
    RDSDalek(),
    DynamoDBDalek(),
//networking   
    EC2EIPDalek(),
    SGRulesDalek(),
    SGDalek()
//    EC2Dalek(),
//    VPCDalek(),
//    SGDalek()
  ).foreach(_.fly)

  override def toString = region.toString

  
}