
package aws.daleks

import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import com.amazonaws.services.s3.model.Bucket
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.rds.AmazonRDSClient
import com.amazonaws.services.rds.model.DBInstance
import com.amazonaws.services.ec2.model.Volume
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.regions.ServiceAbbreviations
import com.amazonaws.services.s3.model.{ Region => S3Region }
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest
import com.amazonaws.services.ec2.model.DeleteVolumeRequest
import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.DeleteQueueRequest
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.Topic
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.elasticmapreduce.model.Application
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationRequest
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.cloudformation.model.DeleteStackRequest
import scala.util.Try
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription
import org.apache.http.client.CredentialsProvider
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.ec2.model.KeyPairInfo
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest
import com.amazonaws.services.elasticache.AmazonElastiCacheClient
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient
import com.amazonaws.services.elasticache.model.CacheCluster
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest
import com.amazonaws.services.elasticmapreduce.model.Cluster
import com.amazonaws.services.elasticmapreduce.model.TerminateJobFlowsRequest
import com.amazonaws.services.route53.AmazonRoute53Client
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest
import com.amazonaws.services.route53.model.ResourceRecordSet
import com.amazonaws.services.route53.model.HostedZone
import java.util.Properties
import java.io.InputStreamReader
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentsRequest
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription
import com.amazonaws.services.elasticbeanstalk.model.TerminateEnvironmentRequest
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentStatus

//TODO: Laxy
class EagerRegionDalek(credentials: AWSCredentialsProvider, region: Region) {
  println("* Exterminating Region " + region.getName())

  val s3 = {
    val s3 = new AmazonS3Client(credentials);
    val endpoint = region.getServiceEndpoint(ServiceAbbreviations.S3);
    s3.setEndpoint(endpoint);
    lockedToRegion(s3)
  }

  val dynamo = lockedToRegion(new AmazonDynamoDBClient(credentials))
  val ec2 = lockedToRegion(new AmazonEC2Client(credentials))
  val rds = lockedToRegion(new AmazonRDSClient(credentials))
  val sqs = lockedToRegion(new AmazonSQSClient(credentials))
  val sns = lockedToRegion(new AmazonSNSClient(credentials))
  val beanstalk = lockedToRegion(new AWSElasticBeanstalkClient(credentials))
  val cloudformaiton = lockedToRegion(new AmazonCloudFormationClient(credentials))
  val ecache = lockedToRegion(new AmazonElastiCacheClient(credentials))
  val emr = lockedToRegion(new AmazonElasticMapReduceClient(credentials))

  def lockedToRegion[T <: AmazonWebServiceClient](client: T): T = {
    client.setRegion(region)
    client
  }

  def buckets = (s3.listBuckets asScala).filter { bucket =>
    val locStr = s3.getBucketLocation(bucket.getName)
    val bucketRegion = S3Region.fromValue(locStr).toAWSRegion()
    bucketRegion.equals(region)
  }

  def objects = buckets.flatMap { bucket =>
    s3.listObjects(bucket.getName).getObjectSummaries() asScala
  }

  type TableName = String
  type QueueURL = String

  def reservations = ec2.describeInstances.getReservations asScala
  def instances = reservations.flatMap { r => r.getInstances asScala }
  def volumes = ec2.describeVolumes.getVolumes.asScala.filter {
    v => !"in-use".equals(v.getState)
  }

  def keypairs = ec2.describeKeyPairs().getKeyPairs().asScala

  def databases = rds.describeDBInstances.getDBInstances asScala
  def tables: Seq[TableName] = dynamo.listTables.getTableNames asScala
  def queues: Seq[QueueURL] = sqs.listQueues.getQueueUrls asScala
  def topics = sns.listTopics.getTopics asScala

  val TERMINATED = EnvironmentStatus.Terminated.toString()
  def envs = beanstalk.describeEnvironments().getEnvironments().asScala filter { e =>    
    ! TERMINATED.equalsIgnoreCase(e.getStatus())
  }

  def apps = try {
    beanstalk.describeApplications.getApplications asScala
  } catch {
    case e: Exception => {
      println("Could not fectch beanstalk applications: " + e.getMessage());
      List.empty
    }
  }

  def stacks = cloudformaiton.describeStacks.getStacks asScala
  def caches = ecache.describeCacheClusters.getCacheClusters asScala
  def clusters = emr.listClusters.getClusters asScala

  def exterminate(x: Any) = x match {
    case o: S3ObjectSummary => {
      println("** Exterminating S3 Object " + o.getKey);
      s3.deleteObject(o.getBucketName, o.getKey)
    }

    case b: Bucket =>
      try {
        println("** Exterminating Bucket " + b.getName)
        s3.deleteBucketPolicy(b.getName())
        s3.deleteBucket(b.getName)
      } catch {
        case e: Exception => println(s"! Failed to exterminate S3 Bucket ${b.getName}: ${e.getMessage()}")
      }

    case i: Instance => try {
      println(s"** Exterminating EC2 Instance ${i.getInstanceId} on region ${region}")
      ec2.terminateInstances(new TerminateInstancesRequest().withInstanceIds(i.getInstanceId))
    } catch {
      case e: Exception => println("! Failed to terminate EC2 Instance" + i.getInstanceId())
    }

    case v: Volume => {
      println("** Exterminating Volume " + v.getVolumeId())
      ec2.deleteVolume(new DeleteVolumeRequest().withVolumeId(v.getVolumeId()));
    }

    case db: DBInstance => {
      println("** Exterminating RDS Database " + db.getDBInstanceIdentifier)
      val delReq = new DeleteDBInstanceRequest
      delReq.setDBInstanceIdentifier(db.getDBInstanceIdentifier())
      delReq.setSkipFinalSnapshot(true);
      rds.deleteDBInstance(delReq)
    }

    case t: Topic => {
      println("** Exterminating SNS Topic " + t.getTopicArn())
      sns.deleteTopic(t.getTopicArn())
    }

    case stack: Stack =>
      try {
        println("** Exterminating CloudFormation Stack " + stack.getStackName())
        cloudformaiton.deleteStack(new DeleteStackRequest().withStackName(stack.getStackName()))
      } catch {
        case e: Exception => println(s"! Failed to exterminate Beanstalk Application ${stack.getStackName}: ${e.getMessage()}")
      }

    case k: KeyPairInfo => try {
      println("** Exterminating KeyPair " + k.getKeyName())
      ec2.deleteKeyPair(new DeleteKeyPairRequest(k.getKeyName()))
    } catch {
      case e: Exception => println(s"! Failed to exterminate KeyPair ${k.getKeyName()}: ${e.getMessage()}")
    }

    case c: CacheCluster => try {
      println("** Exterminating Cache Cluster " + c.getCacheClusterId())
      ecache.deleteCacheCluster(new DeleteCacheClusterRequest().withCacheClusterId(c.getCacheClusterId()))
    } catch {
      case e: Exception => println(s"! Failed to exterminate Cache Cluster ${c.getCacheClusterId()}: ${e.getMessage()}")
    }

    case _ => {
      println("Can't Exterminate the Unknown ")
    }
  }

  def exterminateTable(t: TableName) = {
    println("** Exterminating DyanmoDB Table " + t)
    dynamo.deleteTable(t)
  }

  def exterminateQueue(q: QueueURL) = {
    println("** Esterminating SQS Queue " + q)
    sqs.deleteQueue(new DeleteQueueRequest().withQueueUrl(q))
  }

  def exterminateJobFlows(ids: Seq[String]) = try {
    if (!ids.isEmpty) {
      println("** Exterminating Clusters " + ids.mkString(","))
      val req = new TerminateJobFlowsRequest
      req.setJobFlowIds(ids asJava)
      emr.terminateJobFlows(req)
    }
  } catch {
    case e: Exception => println(s"! Failed to exterminate Clusters ${ids.mkString(",")}: ${e.getMessage()}")
  }

  def exterminateEnv(env: EnvironmentDescription) =
    try {
      val envName = env.getEnvironmentName()
      println(s"** Exterminating Beanstalk Environment ${envName} [${env.getStatus()} ] ")
      beanstalk.terminateEnvironment(new TerminateEnvironmentRequest()
      	.withEnvironmentName(envName)
      	.withTerminateResources(true))
    } catch {
      case e: Exception => println(s"! Failed to exterminate Beanstalk Environment ${env.getEnvironmentName()} [id: ${env.getEnvironmentId} ]: ${e.getMessage()}");
    }
    
  def exterminateApp(app: ApplicationDescription) =
    try {
      println("** Exterminating Beanstalk Application " + app.getApplicationName())
      beanstalk.deleteApplication(new DeleteApplicationRequest().withApplicationName(app.getApplicationName()))
    } catch {
      case e: Exception => println(s"! Failed to exterminate Beanstalk Application ${app.getApplicationName()}: ${e.getMessage()}")
    }

  def exterminate: Unit = {
    envs foreach exterminateEnv
    apps foreach exterminateApp
    stacks foreach exterminate
    caches foreach exterminate
    exterminateJobFlows(clusters map { _.getId() })
    instances ++
      volumes ++
      databases ++
      topics foreach exterminate
    objects foreach exterminate
    buckets foreach exterminate
    tables foreach exterminateTable
    queues foreach exterminateQueue
  }

}