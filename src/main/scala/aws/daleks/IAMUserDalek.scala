package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.services.identitymanagement.model._
import com.amazonaws.services.identitymanagement.model.DeleteLoginProfileRequest
import com.amazonaws.services.identitymanagement.model.DeleteUserRequest
import com.amazonaws.services.identitymanagement.model.ListUserPoliciesRequest
import com.amazonaws.services.identitymanagement.model.User
import rx.lang.scala._
import scala.collection.JavaConverters._

case class IAMUserDalek(implicit region: Region) extends RxDalek[User] {
  val iam = new AmazonIdentityManagementClient

  val selfUser = iam.getUser().getUser

  override def observe: Observable[User] = iam
    .listUsers()
    .getUsers
    .asScala
    .toObservable

  def isSpared(u: User) = {
    val isSelf = selfUser == u
    val isLOTRLoc = isLOTR(u.getUserName)
    true || isSelf || isLOTRLoc //TODO: kill dependencies
  }

  override def exterminate(u: User): Unit = if (!isSpared(u)) {
    IAMAccessKeyDalek(u).fly
    //TODO: leaveGroups(u)
    detachUserPolicies(u)
    iam.deleteLoginProfile(new DeleteLoginProfileRequest().withUserName(u.getUserName))
    iam.deleteUser(new DeleteUserRequest().withUserName(u.getUserName))
  }

  

  def detachUserPolicies(u: User): Unit = {
    val userPolicies = iam.listUserPolicies(
      new ListUserPoliciesRequest().withUserName(u.getUserName))
      .getPolicyNames
      .asScala
    userPolicies.foreach(up => iam.detachUserPolicy(new DetachUserPolicyRequest().withUserName(u.getUserName).withPolicyArn(up)))
  }

  override def describe(u: User): Map[String, String] = Map(
    ("userName" -> u.getUserName),
    ("userSpared" -> isSpared(u).toString()))

}