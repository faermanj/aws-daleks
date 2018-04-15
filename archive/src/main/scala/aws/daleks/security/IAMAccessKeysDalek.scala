package aws.daleks.security

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest
import com.amazonaws.services.identitymanagement.model.DeleteAccessKeyRequest
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.services.identitymanagement.model.User
import aws.daleks.RxDalek
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder

case class IAMAccessKeyDalek(user:User) extends RxDalek[AccessKeyMetadata] {
  val iam = IAM.iam
  
  override def observe:Observable[AccessKeyMetadata] = iam.listAccessKeys(
        new ListAccessKeysRequest().withUserName(user.getUserName))
    .getAccessKeyMetadata()
    .asScala
    .toObservable
    
  override def exterminate(ar:AccessKeyMetadata):Unit =
    iam.deleteAccessKey(new DeleteAccessKeyRequest().withAccessKeyId(ar.getAccessKeyId))
  
  override def describe(ar:AccessKeyMetadata):Map[String,String] = Map(
    ("user" -> user.getUserName),
    ("accessKeyId" -> ar.getAccessKeyId)
  )
    
}