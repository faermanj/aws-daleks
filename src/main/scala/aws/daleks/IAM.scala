package aws.daleks

import scala.collection.JavaConverters._
import com.amazonaws.services.identitymanagement.model.InstanceProfile
import com.amazonaws.services.ec2.model.IamInstanceProfile
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesRequest
import com.amazonaws.services.identitymanagement.model.GetInstanceProfileRequest

object IAM {
  val iam = new AmazonIdentityManagementClient

  val roles = scala.collection.mutable.SortedSet[String]()
  val instanceProfiles = scala.collection.mutable.SortedSet[String]()

  def setMercyOnRole(roleName: String) = this.synchronized {
    roles += roleName
  }

  def isMercyOnRole(roleName: String) = this.synchronized {
    roles.contains(roleName)
  }

  def setMercyOnInstanceProfile(iip: IamInstanceProfile) = this.synchronized {
    
    instanceProfiles += iip.getId
  }

  def isMercyOnInstanceProfile(profileId: String) = this.synchronized {
    instanceProfiles.contains(profileId)
  }

}