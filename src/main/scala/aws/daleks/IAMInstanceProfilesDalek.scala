package aws.daleks

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import java.util.List
import java.util.Collections
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.services.identitymanagement.model.InstanceProfile
import com.amazonaws.services.identitymanagement.model.DeleteInstanceProfileRequest
import com.amazonaws.services.identitymanagement.model.DetachRolePolicyRequest
import com.amazonaws.services.identitymanagement.model.RemoveRoleFromInstanceProfileRequest

case class IAMInstanceProfilesDalek(implicit region: Region) extends RxDalek[InstanceProfile] {
  val iam = new AmazonIdentityManagementClient

  override def list(): List[InstanceProfile] = iam.listInstanceProfiles().getInstanceProfiles
  override def mercy(ar: InstanceProfile) = IAM.isMercyOnInstanceProfile(ar.getInstanceProfileId)

  override def exterminate(ar: InstanceProfile) = {
    removeRoles(ar)
    iam.deleteInstanceProfile(new DeleteInstanceProfileRequest().withInstanceProfileName(ar.getInstanceProfileName))
  }

  def removeRoles(ar: InstanceProfile) = for (
    role <- ar.getRoles.asScala
  ) iam.removeRoleFromInstanceProfile(
    new RemoveRoleFromInstanceProfileRequest()
      .withInstanceProfileName(ar.getInstanceProfileName)
      .withRoleName(role.getRoleName))

  override def describe(ar: InstanceProfile) = Map(
    ("instanceProfileName" -> ar.getInstanceProfileName))

}