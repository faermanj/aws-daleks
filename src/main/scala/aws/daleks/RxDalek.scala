package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.AmazonWebServiceClient
import rx.lang.scala._

abstract class RxDalek[T](implicit region: Region) extends Dalek{
  
  def observe:Observable[T] = Observable.empty
  def exterminate(t:T):Unit = {}
  def describe(t:T):Map[String,String] = Map()
  
  def flyDependencies(t:T) = {}
    
  def fly = for (target <- observe ){
        flyDependencies(target)
        speak(describe(target)+("region" -> region.toString()))
        if (! Dalek.good) exterminate(target)    
  }
    
  def speak(landing:Map[String,String]):Unit = {
    println(landing.map{ case (key, value) => s"${key}=${value}" }.mkString(", "))
  }
}