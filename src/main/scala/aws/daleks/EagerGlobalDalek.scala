package aws.daleks

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.auth.AWSCredentialsProvider
import scala.collection.JavaConverters._
import com.amazonaws.services.identitymanagement.model.DeleteUserRequest
import com.amazonaws.services.identitymanagement.model.DeleteGroupRequest
import com.amazonaws.services.identitymanagement.model.DeleteRoleRequest
import com.amazonaws.services.route53.AmazonRoute53Client
import com.amazonaws.services.identitymanagement.model.DeleteAccessKeyRequest
import java.util.Properties
import java.io.InputStreamReader
import com.amazonaws.services.identitymanagement.model.ListGroupsForUserRequest
import com.amazonaws.services.identitymanagement.model.RemoveUserFromGroupRequest
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest
import com.amazonaws.services.identitymanagement.model.ListGroupPoliciesRequest
import com.amazonaws.services.identitymanagement.model.DeleteGroupPolicyRequest
import com.amazonaws.services.identitymanagement.model.ListUserPoliciesRequest
import com.amazonaws.services.identitymanagement.model.DeleteUserPolicyRequest

class EagerGlobalDalek(credentials: AWSCredentialsProvider) {

  lazy val key = {
    val p = new Properties
    val stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties")
    val reader = new InputStreamReader(stream)
    p.load(reader)
    val k = p.getProperty("accessKey")
    k
  }
  val iam = new AmazonIdentityManagementClient(credentials);
  val r53 = new AmazonRoute53Client(credentials)

  def users = iam.listUsers().getUsers().asScala filter { u =>
    !"dalek".equals(u.getUserName())
  }

  def roles = iam.listRoles().getRoles() asScala
  def groups = iam.listGroups().getGroups() asScala

  def zones = r53.listHostedZones.getHostedZones.asScala.filter { z =>
    !(z.getName().endsWith("awstc.com."))
  }

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
        
        iam.deleteUser(new DeleteUserRequest().withUserName(username))
      } catch {
        case e: Exception => println("Failed to exterminate user [" + user.getUserName() + "]: " + e.getMessage())
      }
    }

    println("Exterminating Groups")
    groups.foreach { group =>
      iam.listGroupPolicies(new ListGroupPoliciesRequest().withGroupName(group.getGroupName())).getPolicyNames().asScala foreach {
        policy => {
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
      try{
           println(s"Exterminating role [${role.getRoleName()}]")
    	   iam.deleteRole(new DeleteRoleRequest().withRoleName(role.getRoleName()))
      }catch {
           case e: Exception => println(s"! Failed to exterminate Role ${role.getRoleName()}: ${e.getMessage()}")
      }
    }

    println("Exterminating Hosted Zones")
    zones.foreach { z =>
      try {
        println("** Exterminating HostedZone " + z.getName)
        // val records = r53.listResourceRecordSets(new ListResourceRecordSetsRequest().withHostedZoneId(z.getId())).getResourceRecordSets() asScala
        // records.foreach
        // TODO
      } catch {
        case e: Exception => println(s"! Failed to exterminate Zone ${z.getName()}: ${e.getMessage()}")
      }
    }

  }
}