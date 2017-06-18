package aws.daleks

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region
import java.util.List
import java.util.Collections

case class PrototypeDalek(implicit region: Region) extends RxDalek[Any] {

  override def list() = Collections.emptyList()
  override def exterminate(ar: Any) = {}
  override def describe(ar: Any) = Map()

}