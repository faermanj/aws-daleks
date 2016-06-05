package aws.daleks

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import scala.collection.JavaConverters._
import com.amazonaws.services.identitymanagement.model.User
import com.amazonaws.services.identitymanagement.model.DeleteUserRequest
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult
import com.amazonaws.services.identitymanagement.model.DeleteAccessKeyRequest
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata

case class IAMDalek() extends Dalek {
  val iam = new AmazonIdentityManagementClient()

  def fly = {
    flyUsers
  }

  def flyUsers = iam.listUsers()
    .getUsers
    .asScala
    .filter { user =>
      !"dalek".equals(user.getUserName)
    }.foreach { fly(_) }

  def fly(user: User): Unit = {
    val username = user.getUserName

    val aks = iam.listAccessKeys(new ListAccessKeysRequest().withUserName(username))
    fly(username, aks)
  }

  def fly(username: String, aks: ListAccessKeysResult): Unit = {
    exterminate(username, aks)
    if (aks.isTruncated()) {
      val nextAks = iam.listAccessKeys(
        new ListAccessKeysRequest()
          .withUserName(username)
          .withMarker(aks.getMarker))
      fly(username, nextAks)
    }
  }

  def exterminate(username: String, aks: ListAccessKeysResult): Unit =
    aks.getAccessKeyMetadata
      .asScala
      .foreach(exterminate(username, _))

  def exterminate(username: String, ak: AccessKeyMetadata): Unit = {
    val akId = ak.getAccessKeyId
    println(s"${username} | ${akId}")
    exterminate { () =>
      iam.deleteAccessKey(
        new DeleteAccessKeyRequest()
          .withUserName(username)
          .withAccessKeyId(akId))
    }
  }

  def exterminate(user: User): Unit = {
    val username = user.getUserName
    println(s"${username}")
    exterminate { () =>
      iam.deleteUser(
        new DeleteUserRequest().withUserName(username))
    }
  }
}