package aws.daleks.eager

import java.io.InputStreamReader
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import java.util.Properties
import com.amazonaws.auth.AWSCredentialsProvider
import scala.collection.JavaConverters._
import com.amazonaws.services.identitymanagement.model.DeleteRoleRequest
import com.amazonaws.services.identitymanagement.model.DeleteGroupRequest
import com.amazonaws.services.identitymanagement.model.ListGroupsForUserRequest
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest
import com.amazonaws.services.identitymanagement.model.ListUserPoliciesRequest
import com.amazonaws.services.identitymanagement.model.DeleteAccessKeyRequest
import com.amazonaws.services.identitymanagement.model.ListGroupPoliciesRequest
import com.amazonaws.services.identitymanagement.model.DeleteUserRequest
import com.amazonaws.services.identitymanagement.model.DeleteUserPolicyRequest
import com.amazonaws.services.identitymanagement.model.RemoveUserFromGroupRequest
import com.amazonaws.services.identitymanagement.model.DeleteGroupPolicyRequest
import com.amazonaws.services.identitymanagement.model.DeleteLoginProfileRequest
import com.amazonaws.services.identitymanagement.model.GetLoginProfileRequest
import com.amazonaws.services.identitymanagement.model.DeleteInstanceProfileRequest
import com.amazonaws.services.identitymanagement.model.RemoveRoleFromInstanceProfileRequest
import com.amazonaws.services.identitymanagement.model.ListInstanceProfilesForRoleRequest
import com.amazonaws.services.identitymanagement.model.GetRolePolicyRequest
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesRequest
import com.amazonaws.services.identitymanagement.model.DeleteRolePolicyRequest

class EagerIAMDalek(implicit credentials: AWSCredentialsProvider) extends Dalek {
  lazy val key = {
    val p = new Properties
    val stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties")
    val reader = new InputStreamReader(stream)
    p.load(reader)
    val k = p.getProperty("accessKey")
    k
  }
  val iam = new AmazonIdentityManagementClient(credentials);

  def users = iam.listUsers().getUsers().asScala filter { u =>
    !"dalek".equals(u.getUserName())
  }

  def roles = iam.listRoles().getRoles() asScala
  def groups = iam.listGroups().getGroups() asScala

  def exterminate = {

    println("Exterminating Users")
    users.foreach { user =>
      try {

        val username = user.getUserName()

        val userKeys = {
          val req = new ListAccessKeysRequest()
          req.setUserName(username)
          val aks = iam.listAccessKeys(req)
          aks.getAccessKeyMetadata.asScala filter { (k => k.getAccessKeyId() != key) }
        }

        userKeys foreach {
          k =>
            {
              println(s"Exterminating access key [$k.getAccessKeyId()] from user [$username]")
              iam.deleteAccessKey(new DeleteAccessKeyRequest().withUserName(username).withAccessKeyId(k.getAccessKeyId()))
            }
        }

        val gs = iam.listGroupsForUser(new ListGroupsForUserRequest().withUserName(username)).getGroups().asScala
        gs foreach { g =>
          {
            println(s"Exterminating membership of user [$username] in group [${g.getGroupName()}]")
            iam.removeUserFromGroup(new RemoveUserFromGroupRequest().withGroupName(g.getGroupName()).withUserName(username))
          }
        }

        val ps = iam.listUserPolicies(new ListUserPoliciesRequest().withUserName(username)).getPolicyNames().asScala
        ps.foreach { p =>
          println(s"Exterminating policy [$p] of user [$username]")
          iam.deleteUserPolicy(new DeleteUserPolicyRequest().withUserName(username).withPolicyName(p))
        }

        val olp = Option(iam.getLoginProfile(new GetLoginProfileRequest().withUserName(username)).getLoginProfile())
        olp foreach { lp =>
          println(s"Exterminating login profile created at [${lp.getCreateDate}] of user [$username]")
          iam.deleteLoginProfile(new DeleteLoginProfileRequest().withUserName(username))
        }

        iam.deleteUser(new DeleteUserRequest().withUserName(username))
      } catch {
        case e: Exception => println("Failed to exterminate user [" + user.getUserName() + "]: " + e.getMessage())
      }
    }

    println("Exterminating Groups")
    groups.foreach { group =>
      iam.listGroupPolicies(new ListGroupPoliciesRequest().withGroupName(group.getGroupName())).getPolicyNames().asScala foreach {
        policy =>
          {
            println(s"Exterminating policy [$policy] in group [${group.getGroupName()}]")
            iam.deleteGroupPolicy(new DeleteGroupPolicyRequest().withGroupName(group.getGroupName()).withPolicyName(policy))
          }
      }
      println(s"Exterminating group [${group.getGroupName()}]")
      iam.deleteGroup(new DeleteGroupRequest().withGroupName(group.getGroupName()))
    }

    println("Exterminating Roles")
    roles.foreach { role =>
      //TODO: Exterminate roles with policies and instances

      val roleName = role.getRoleName()

      println(s"Exterminating Instance Profiles for Role [$roleName]")
      val ipsfr = iam.listInstanceProfilesForRole(
        new ListInstanceProfilesForRoleRequest()
          .withRoleName(roleName))
        .getInstanceProfiles().asScala
      ipsfr foreach { ip =>
        println(s"Removing instance profile [${ip.getInstanceProfileName()}] from role [$roleName]")
        iam.removeRoleFromInstanceProfile(
          new RemoveRoleFromInstanceProfileRequest()
            .withInstanceProfileName(ip.getInstanceProfileName)
            .withRoleName(roleName))
      }
      
      val policies = iam.listRolePolicies(new ListRolePoliciesRequest().withRoleName(roleName)).getPolicyNames().asScala
      policies foreach {policy =>
        println(s"Deleting role policy [$policy] from role [$roleName]")
        iam.deleteRolePolicy(new DeleteRolePolicyRequest().withPolicyName(policy).withRoleName(roleName) ) 
      }

      println(s"Exterminating role [${role.getRoleName()}]")
      iam.deleteRole(new DeleteRoleRequest().withRoleName(roleName))

    }

    println("Exterminating instance profiles")
    val ips = iam.listInstanceProfiles().getInstanceProfiles().asScala
    ips foreach { ip =>
      println(s"Exterminating Instance Profile [${ip.getInstanceProfileName()}]")
      iam.deleteInstanceProfile(new DeleteInstanceProfileRequest().withInstanceProfileName(ip.getInstanceProfileName()))
    }

  }
}