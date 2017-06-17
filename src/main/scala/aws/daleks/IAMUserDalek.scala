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
import scala.util.Try

case class IAMUserDalek(implicit region: Region) extends RxDalek[User] {
  val iam = new AmazonIdentityManagementClient

  val selfUser = iam.getUser().getUser

  override def observe: Observable[User] = iam
    .listUsers()
    .getUsers
    .asScala
    .toObservable

  override def mercy(u: User) = {
    val isSelf = selfUser == u
    val isSpared = isSparedName(u.getUserName)
    val mercy = isSelf || isSpared
    if (mercy) iam.listAttachedUserPolicies(
        new ListAttachedUserPoliciesRequest().withUserName(u.getUserName()))
        .getAttachedPolicies
        .asScala
        .map{ up => up.getPolicyName }
        .foreach{ IAM.setMercyOnRole }
    mercy
  }

  override def exterminate(u: User): Unit = {
    IAMAccessKeyDalek(u).fly
    leaveGroups(u)
    detachUserPolicies(u)
    exterminateLoginProfile(u: User)
    iam.deleteUser(new DeleteUserRequest().withUserName(u.getUserName))
  }

  def exterminateLoginProfile(u: User): Unit = Try {
    iam.getLoginProfile(new GetLoginProfileRequest().withUserName(u.getUserName)).getLoginProfile
  }.foreach { lp =>
    iam.deleteLoginProfile(new DeleteLoginProfileRequest().withUserName(lp.getUserName))
  }

  def leaveGroups(u: User): Unit =
    iam.listGroupsForUser(
      new ListGroupsForUserRequest().withUserName(u.getUserName))
      .getGroups
      .asScala
      .foreach { g =>
        iam.removeUserFromGroup(new RemoveUserFromGroupRequest()
          .withGroupName(g.getGroupName)
          .withUserName(u.getUserName))
      }

  def detachUserPolicies(u: User): Unit = {
    val userPolicies = iam.listAttachedUserPolicies(
      new ListAttachedUserPoliciesRequest().withUserName(u.getUserName()))
      .getAttachedPolicies
      .asScala
    userPolicies.foreach(ap => iam.detachUserPolicy(
      new DetachUserPolicyRequest().withUserName(u.getUserName).withPolicyArn(ap.getPolicyArn)))
  }

  override def describe(u: User): Map[String, String] = Map(
    ("userName" -> u.getUserName))

}