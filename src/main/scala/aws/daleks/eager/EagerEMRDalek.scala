package aws.daleks.eager

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Region
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.DeleteQueueRequest
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient
import com.amazonaws.services.elasticmapreduce.model.TerminateJobFlowsRequest
import aws.daleks.util.Humid

class EagerEMRDalek(implicit region: Region, credentials: AWSCredentialsProvider) extends Dalek {
  val emr = withRegion(new AmazonElasticMapReduceClient(credentials), region)

  def exterminate = {
    val clusters = emr.listClusters.getClusters.asScala

    clusters map { _.getId() } foreach { id =>
      try {        
        info(this,s"Exterminating Clusters $id")
        val req = new TerminateJobFlowsRequest
        req.setJobFlowIds(List(id).asJava)
        Humid {
          emr.terminateJobFlows(req)
        }
      } catch {
        case e: Exception => println(s"! Failed to exterminate Clusters ${id}: ${e.getMessage()}")
      }
    }
  }
}