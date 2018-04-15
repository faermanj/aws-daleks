package aws.daleks.storage

import com.amazonaws.services.s3.model.S3VersionSummary
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import rx.lang.scala.Observable
import com.amazonaws.services.s3.model.ListVersionsRequest
import com.amazonaws.services.s3.model.VersionListing
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.model.Bucket
import aws.daleks.RxDalek

case class S3ObjectVersionDalek(bucket: Bucket, reg:Region) extends RxDalek[S3VersionSummary] {
  val s3 = AmazonS3ClientBuilder.standard().withRegion(reg.getName).build()

  def streamVersions = versions(s3.listVersions(new ListVersionsRequest()))

  def versions(vsum: VersionListing): Stream[S3VersionSummary] =
    vsum.getVersionSummaries.asScala.toStream append {
      if (vsum.isTruncated())
        versions(s3.listNextBatchOfVersions(vsum))
      else
        Stream.empty
    }

  override def observe: Observable[S3VersionSummary] = Observable.from(
    versions(s3.listVersions(
      new ListVersionsRequest().withBucketName(bucket.getName))))

  override def exterminate(v: S3VersionSummary): Unit = s3.deleteVersion(v.getBucketName, v.getKey, v.getVersionId)

  override def describe(v: S3VersionSummary): Map[String, String] = Map(
    ("bucket" -> v.getBucketName),
    ("key" -> v.getKey),
    ("versionId" -> v.getVersionId))

}