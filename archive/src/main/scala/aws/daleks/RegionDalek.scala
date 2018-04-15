package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.auth.AWSCredentialsProvider
import aws.daleks.compute._
import aws.daleks.database._
import aws.daleks.analytics._
import aws.daleks.ai._
import aws.daleks.networking._
import aws.daleks.management._
import com.amazonaws.regions.Regions

case class RegionDalek() extends RxDalek[Region] {

  override def fly(region: Region): String = {
    List(
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
      RedshiftDalek(),
      RDSDalek(),
      DynamoDBDalek(),
      //networking   
      EC2EIPDalek(),
      IGWDalek(),
      SGRulesDalek(),
      SGDalek()).foreach { dalek =>
        dalek.region = region
        dalek.fly
      }
    "success"
  }
}