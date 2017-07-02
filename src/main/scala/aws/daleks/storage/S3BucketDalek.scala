package aws.daleks

import com.amazonaws.services.s3.AmazonS3Client
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.model.Bucket
import com.amazonaws.services.s3.model.ObjectListing
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.amazonaws.regions.Region
import com.amazonaws.regions.ServiceAbbreviations
import com.amazonaws.services.s3.model.{ Region => S3Region }
import com.amazonaws.services.s3.model.ListBucketsRequest
import com.amazonaws.services.s3.model.VersionListing
import com.amazonaws.services.s3.model.S3VersionSummary
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import rx.lang.scala._
import com.amazonaws.services.s3.model.DeleteBucketRequest
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest
import com.amazonaws.services.s3.model.BucketVersioningConfiguration
import aws.daleks.storage.S3ObjectVersionDalek

case class S3BucketDalek(implicit region: Region) extends RxDalek[Bucket] {
  val s3 = AmazonS3ClientBuilder.defaultClient()

  val regionBuckets = s3.listBuckets().asScala.filter { bucket =>
    val locStr = s3.getBucketLocation(bucket.getName)
    val bucketRegion = S3Region.fromValue(locStr).toAWSRegion()
    bucketRegion.equals(region)
  }
  
  override def observe: Observable[Bucket] = regionBuckets.toObservable
  override def describe(bucket: Bucket): Map[String, String] = Map(
      "bucket" -> bucket.getName
  )
  
  override def exterminate(bucket: Bucket) = {
    val bucketName = bucket.getName
    s3.setBucketVersioningConfiguration(
          new SetBucketVersioningConfigurationRequest(bucket.getName,
              new BucketVersioningConfiguration(BucketVersioningConfiguration.SUSPENDED)))
    s3.deleteBucketPolicy(bucketName)
    s3.deleteBucket(bucketName)
  }
  
  override def flyDependencies(bucket: Bucket) = List(S3ObjectVersionDalek(bucket)).foreach(_.fly)

}