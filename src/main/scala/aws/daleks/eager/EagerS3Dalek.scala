package aws.daleks.eager

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.regions.ServiceAbbreviations
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.model.{ Region => S3Region }
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.amazonaws.services.s3.model.Bucket
import aws.daleks.util.Humid

class EagerS3Dalek(implicit region: Region, credentials: AWSCredentialsProvider)
  extends Dalek {

  val s3 = {
    val s3 = new AmazonS3Client(credentials);
    val endpoint = region.getServiceEndpoint(ServiceAbbreviations.S3);
    s3.setEndpoint(endpoint);
    withRegion(s3, region)
  }

  def buckets = (s3.listBuckets.asScala).filter { bucket =>
    val name = bucket.getName
    val keep =  name.startsWith("logs")  || name.startsWith("billing") || name.startsWith("share")
    !keep
  }.filter { bucket =>
    val locStr = s3.getBucketLocation(bucket.getName)
    val bucketRegion = S3Region.fromValue(locStr).toAWSRegion()
    bucketRegion.equals(region)
  }

  def exterminate = buckets foreach { bucket =>
    val bucketName = bucket.getName
    //TODO: Support > 1000 Objects
    val objects = s3.listObjects(bucketName).getObjectSummaries.asScala.par
    objects.foreach { o =>
      println("** Exterminating S3 Object " + bucket.getName + "/" + o.getKey);
      Humid {
        s3.deleteObject(o.getBucketName, o.getKey)
      }
    }
    val versions = s3.listVersions(bucketName, "").getVersionSummaries().asScala.par
    versions.foreach { v =>
      println("** Exterminating S3 Version " + bucket.getName + "/" + v.getKey() + " v" + v.getVersionId);
      Humid {
        s3.deleteVersion(bucketName, v.getKey, v.getVersionId)
      }
    }

    try {
      println("** Exterminating S3 Bucket Policy " + bucket.getName)
      Humid { s3.deleteBucketPolicy(bucket.getName()) }
      println("** Exterminating S3 Bucket " + bucket.getName)
      Humid { s3.deleteBucket(bucket.getName) }
    } catch {
      case e: Exception => println(s"! Failed to exterminate S3 Bucket ${bucket.getName}: ${e.getMessage()}")
    }
  }

}