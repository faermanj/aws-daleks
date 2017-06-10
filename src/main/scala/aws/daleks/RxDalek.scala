package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.AmazonWebServiceClient
import rx.lang.scala._

abstract class RxDalek[T](implicit region: Region) extends Dalek{
  def toLanding(t:Tuple2[String,String]*):Landing = Map("region" -> region.toString()) ++ Map(t:_*)
  
  def observe:Observable[T]
  def exterminate(t:T):Unit
  def describe(t:T):Map[String,String]
  
  def fly = for (target <- observe ){
        speak(describe(target))
        if (! Dalek.good) exterminate(target)    
  }
    
  def speak(landing:Map[String,String]):Unit = {
    println(landing.map{ case (key, value) => s"${key}=${value}" }.mkString(" | "))
  }
}