package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.AmazonWebServiceClient
import rx.lang.scala._
import com.amazonaws.regions.Regions
import aws.lotr._

abstract class RxDalek[T](implicit region: Region) extends Dalek{
  val extra = scala.collection.mutable.Map[String, String]()
  
  if (region != null) extra += ("region"->region.toString())
  
  def observe:Observable[T] = Observable.empty
  def exterminate(t:T):Unit = {}
  def describe(t:T):Map[String,String] = Map()
  
  def flyDependencies(t:T) = {}
    
  def fly = for (target <- observe ){
        flyDependencies(target)
        speak(describe(target))
        if (! Dalek.good) exterminate(target)    
  }
    
  def speak(landing:Map[String,String]):Unit = {
    println((landing ++ extra)
        .toSeq
        .sortWith{ case ((a1,a2),(b1,b2)) => 
          if ("region".equals(a1)) true 
          else if ("region".equals(b1)) false 
          else a1 < b1 }
        .map{ case (key, value) => s"${key}=${value}" }
        .mkString(", "))
  }
  
  def isLOTR(name:String) = locations.contains(name.split("-").head.toUpperCase)
}