package aws.daleks.security

import scala.collection.JavaConverters._
import com.amazonaws.services.ec2.model.IamInstanceProfile
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder

object IAM {
  val iam = AmazonIdentityManagementClientBuilder.defaultClient()
  
  val instanceProfiles = scala.collection.mutable.SortedSet[String]()
  val roles = scala.collection.mutable.SortedSet[String]()
  

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