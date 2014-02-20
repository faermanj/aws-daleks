
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

//TODO: Laxy
class EagerRegionDalek(region: Region) {
  println("* Exterminating Region " + region.getName())
  val credentials = new ClasspathPropertiesFileCredentialsProvider
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

  def databases = rds.describeDBInstances.getDBInstances asScala
  def tables: Seq[TableName] = dynamo.listTables.getTableNames asScala
  def queues: Seq[QueueURL] = sqs.listQueues.getQueueUrls asScala
  def topics = sns.listTopics().getTopics() asScala
  def apps = beanstalk.describeApplications.getApplications asScala
  def stacks = cloudformaiton.describeStacks.getStacks asScala

  def exterminate(x: Any) = x match {
    case o: S3ObjectSummary => {
      println("** Exterminating S3 Object " + o.getKey);
      s3.deleteObject(o.getBucketName, o.getKey)
    }

    case b: Bucket => {
      println("** Exterminating Bucket " + b.getName)
      s3.deleteBucket(b.getName)
    }

    case i: Instance => {
      println("** Exterminating EC2 Instance" + i.getInstanceId)
      try {
        ec2.terminateInstances(new TerminateInstancesRequest().withInstanceIds(i.getInstanceId))
      } catch {
        case e: Exception => println("! Failed to terminate EC2 Instance" + i.getInstanceId())
      }
    }

    case v: Volume => {
      println("** Exterminating Volume " + v.getVolumeId())
      ec2.deleteVolume(new DeleteVolumeRequest().withVolumeId(v.getVolumeId()));
    }

    case db: DBInstance => {
      println("** Exterminating RDS Database" + db.getDBInstanceIdentifier)
      val delReq = new DeleteDBInstanceRequest
      delReq.setDBInstanceIdentifier(db.getDBInstanceIdentifier())
      rds.deleteDBInstance(delReq)
    }

    case t: Topic => {
      println("** Exterminating SNS Topic " + t.getTopicArn())
      sns.deleteTopic(t.getTopicArn())
    }

    case app: Application => {
      println("** Exterminating Beanstalk Application " + app.getName)
      beanstalk.deleteApplication(new DeleteApplicationRequest().withApplicationName(app.getName()))
    }

    case stack: Stack => {
      println("** Exterminating CloudFormation Stack " + stack.getStackName())
      cloudformaiton.deleteStack(new DeleteStackRequest().withStackName(stack.getStackName()))
    }

    case _ => println("Can't Exterminate the Unknown")
  }

  def exterminateTable(t: TableName) = {
    println("** Exterminating DyanmoDB Table " + t)
    dynamo.deleteTable(t)
  }

  def exterminateQueue(q: QueueURL) = {
    println("** Esterminating SQS Queue " + q)
    sqs.deleteQueue(new DeleteQueueRequest().withQueueUrl(q))
  }

  def exterminate: Unit = {
    stacks foreach exterminate
    apps foreach exterminate
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