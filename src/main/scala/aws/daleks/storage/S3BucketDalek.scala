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

case class S3BucketDalek() extends RxDalek[Bucket] {
  
  val s3 = AmazonS3ClientBuilder.standard().withRegion(regions).build()

  def regionOf(bucket:Bucket) = 
    S3Region.fromValue(s3.getBucketLocation(bucket.getName)).toAWSRegion()
  

  override def observe: Observable[Bucket] = s3.listBuckets().asScala.toObservable
  
  override def describe(bucket: Bucket): Map[String, String] = Map(
      "bucket" -> bucket.getName,
      "region" -> regionOf(bucket).toString()
  )
  
  override def exterminate(bucket: Bucket) = {
    val bucketName = bucket.getName
    s3.setRegion(regionOf(bucket))
    s3.setBucketVersioningConfiguration(
          new SetBucketVersioningConfigurationRequest(bucket.getName,
              new BucketVersioningConfiguration(BucketVersioningConfiguration.SUSPENDED)))
    s3.deleteBucketPolicy(bucketName)
    s3.deleteBucket(bucketName)
  }
  
  override def flyDependencies(bucket: Bucket) = List(
      S3ObjectVersionDalek(bucket).withRegion(region)
   ).foreach(_.fly)

}