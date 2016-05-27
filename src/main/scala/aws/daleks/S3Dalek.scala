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

case class S3Dalek(region: Region) extends Dalek {
  val s3 = new AmazonS3Client

  def exterminate = {
    val buckets = s3.listBuckets
    val regionBuckets = buckets.asScala.filter { bucket =>
      val locStr = s3.getBucketLocation(bucket.getName)
      val bucketRegion = S3Region.fromValue(locStr).toAWSRegion()
      bucketRegion.equals(region)
    }
    regionBuckets.foreach(exterminate(_))
  }

  def exterminate(bucket: Bucket): Unit = {
    val listing = s3.listObjects(bucket.getName)
    exterminate(listing);
    delete(bucket);
  }

  def exterminate(listing: ObjectListing): Unit = {
    val objects = listing.getObjectSummaries.asScala
    objects.foreach(delete(_))
    if (listing.isTruncated()) {
      val nextListing = s3.listNextBatchOfObjects(listing)
      exterminate(nextListing)
    }
  }

  def delete(bucket: Bucket) = {
    println(s"${region} | ${bucket.getName}")
    if (Doctor.allow)
      s3.deleteBucket(bucket.getName)
  }

  def delete(obj: S3ObjectSummary) = {
    println(s"${region} | ${obj.getBucketName} | ${obj.getKey}")
    if (Doctor.allow)
      s3.deleteObject(obj.getBucketName, obj.getKey)
  }
}