package aws.daleks

import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.regions.Region
import com.amazonaws.AmazonServiceException
import scala.util.Try
import rx.lang.scala._

trait Dalek {}

object Dalek {
  var good = true
}
