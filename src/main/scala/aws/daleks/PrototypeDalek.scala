package aws.daleks

import rx.lang.scala._
import scala.collection.JavaConverters._
import com.amazonaws.regions.Region

case class PrototypeDalek(implicit region: Region)  extends RxDalek[Any] {
 
  override def observe:Observable[Any] = Observable.empty
  override def exterminate(t:Any):Unit = {}
  override def describe(t:Any):Map[String,String] = Map()
    
}