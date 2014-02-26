package aws.daleks

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.auth.AWSCredentialsProvider
import scala.collection.JavaConverters._
import com.amazonaws.services.identitymanagement.model.DeleteUserRequest
import com.amazonaws.services.identitymanagement.model.DeleteGroupRequest
import com.amazonaws.services.identitymanagement.model.DeleteRoleRequest
import com.amazonaws.services.route53.AmazonRoute53Client

class EagerGlobalDalek(credentials: AWSCredentialsProvider) {
  val iam = new AmazonIdentityManagementClient(credentials);
  val r53 = new AmazonRoute53Client(credentials)

  def users = iam.listUsers().getUsers() asScala
  def roles = iam.listRoles().getRoles() asScala
  def groups = iam.listGroups().getGroups() asScala
  def zones = r53.listHostedZones.getHostedZones.asScala.filter { z => 
    !(z.getName().endsWith("awstc.com."))
  }

  def exterminate = {
    users.foreach { user =>
      iam.deleteUser(new DeleteUserRequest().withUserName(user.getUserName()))
    }
    groups.foreach { group =>
      iam.deleteGroup(new DeleteGroupRequest().withGroupName(group.getGroupName()))
    }

    roles.foreach { role =>
      iam.deleteRole(new DeleteRoleRequest().withRoleName(role.getRoleName()))
    }

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