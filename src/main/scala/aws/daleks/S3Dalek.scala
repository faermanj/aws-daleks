package aws.daleks

import com.amazonaws.services.s3.AmazonS3Client
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.model.Bucket
import com.amazonaws.services.s3.model.ObjectListing
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.amazonaws.regions.Region
import com.amazonaws.regions.ServiceAbbreviations
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.s3.model.{ Region => S3Region }
import com.amazonaws.services.s3.model.ListBucketsRequest
import com.amazonaws.services.s3.model.VersionListing
import com.amazonaws.services.s3.model.S3VersionSummary

case class S3Dalek(implicit region: Region) extends Dalek {
  val s3 = new AmazonS3Client

  override def fly = {
    val buckets = s3.listBuckets
    val regionBuckets = buckets.asScala.filter { bucket =>
      val locStr = s3.getBucketLocation(bucket.getName)
      val bucketRegion = S3Region.fromValue(locStr).toAWSRegion()
      bucketRegion.equals(region)
    }
    regionBuckets.foreach(fly(_))
  }

  def fly(bucket: Bucket): Unit = {
    val bucketName = bucket.getName    
    val objects = s3.listObjects(bucketName)
    fly(objects)
    val versions =  s3.listVersions(bucketName, null)
    fly(versions)
    exterminate(bucket)
  }

  def fly(listing: ObjectListing): Unit = {
    val objects = listing.getObjectSummaries.asScala
    objects.foreach(exterminate(_))
    if (listing.isTruncated()) {
      val nextListing = s3.listNextBatchOfObjects(listing)
      fly(nextListing)
    }
  }
  
  def fly(listing: VersionListing): Unit = {
    val versions = listing.getVersionSummaries.asScala
    versions.foreach(exterminate(_))
    if (listing.isTruncated()) {
      val nextListing = s3.listNextBatchOfVersions(listing)
      fly(nextListing)
    }
  }

  def appear(region:String,bucketName:String) = 
    println(s"${region} | ${bucketName}")
    
  def exterminate(bucket: Bucket): Unit = {
    val bucketName = bucket.getName
    appear(region.toString(),bucketName)
    exterminate { ()=> s3.deleteBucketPolicy(bucketName) }
    exterminate { ()=> s3.deleteBucket(bucketName) } 
  }

  def exterminate(obj: S3ObjectSummary):Unit = {
    println(s"${region} | ${obj.getBucketName} | ${obj.getKey}")
    exterminate { ()=> s3.deleteObject(obj.getBucketName, obj.getKey) }
  }
  
  def exterminate(ver: S3VersionSummary):Unit = {
    val bucketName = ver.getBucketName
    val key = ver.getKey
    val versionId = ver.getVersionId
    println(s"${region} | ${bucketName} | ${key} | ${versionId}")
    exterminate { ()=> s3.deleteVersion(bucketName, key, versionId) }
  }
}