package aws.daleks.storage

import scala.collection.JavaConverters.asScalaBufferConverter

import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.Bucket
import com.amazonaws.services.s3.model.BucketVersioningConfiguration
import com.amazonaws.services.s3.model.{ Region => S3Region }
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest

import aws.daleks.RxDalek
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import aws.daleks.Dalek
import com.amazonaws.regions.Regions

case class S3BucketDalek() extends Dalek[Bucket] {
  
  val s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build()

  def regionOf(bucket:Bucket) = 
    S3Region.fromValue(s3.getBucketLocation(bucket.getName)).toAWSRegion()
  

  override def observe: Observable[Bucket] = s3.listBuckets().asScala.toObservable
  
  override def describe(bucket: Bucket): Map[String, String] = Map(
      "bucket" -> bucket.getName,
      "region" -> regionOf(bucket).toString()
  )
  
  override def exterminate(bucket: Bucket) = {
    val bucketRegion = regionOf(bucket)
    val s3reg = AmazonS3ClientBuilder.standard().withRegion(bucketRegion.getName).build()
    val bucketName = bucket.getName
    S3ObjectVersionDalek(bucket,bucketRegion).fly
    s3reg.setRegion(bucketRegion)
    s3reg.setBucketVersioningConfiguration(
          new SetBucketVersioningConfigurationRequest(bucket.getName,
              new BucketVersioningConfiguration(BucketVersioningConfiguration.SUSPENDED)))
    s3reg.deleteBucketPolicy(bucketName)
    s3reg.deleteBucket(bucketName)
  }
  

}