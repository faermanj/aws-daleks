package aws.daleks

import com.amazonaws.regions.Region
import com.amazonaws.AmazonWebServiceClient
import rx.lang.scala._
import com.amazonaws.regions.Regions
import aws.lotr._
import java.util.ArrayList
import java.util.List
import scala.collection.JavaConverters._
import scala.util.Try
import scala.util.Failure
import scala.util.Success

class Dalek[T] {

  val extra = scala.collection.mutable.Map[String, String]()

  def list(): List[T] = new ArrayList()
  def observe: Observable[T] = list().asScala.toObservable

  def exterminate(t: T): Unit = {}
  def describe(t: T): Map[String, String] = Map()
  def flyDependencies(t: T) = {}
  def mercy(t: T) = false
  def isSupported() = true

  def fly: Unit = if (isSupported()) Try {
    for (target <- observe) {
      flyDependencies(target)
      val description = describe(target)
      val result = ("result" -> fly(target))
      speak(description + result)
    }
  } match {
    case Success(s) => {} //speak( Map(("dalek" -> this.getClass.getName), ("result" -> "success")) ++extra)
    case Failure(e) => speak(Map(("dalek" -> this.getClass.getName), ("result" -> s"failure[${e.getMessage}]")) ++ extra)
  }
  else debug(Map(("dalek" -> this.getClass.getName), ("result" -> s"not supported")) ++ extra)

  def fly(target: T): String = if (mercy(target)) "mercy"
  else if (Daleks.good) "good"
  else Try {
    exterminate(target)
  } match {
    case Success(s) => "exterminated"
    case Failure(e) => {
      e.printStackTrace()
      s"failed[${e.getMessage}]"
    }
  }

  def log(description: Map[String, String]): Unit = {
    println((description ++ extra)
      .toSeq
      .sortWith {
        case ((a1, a2), (b1, b2)) =>
          if ("region".equals(a1)) true
          else if ("region".equals(b1)) false
          else if ("result".equals(a1)) false
          else if ("result".equals(b1)) true
          else a1 < b1
      }
      .map { case (key, value) => s"${key}=${value}" }
      .mkString(", "))
  }

  def speak = log _

  def debug(description: Map[String, String]): Unit = {}

  def isLOTR(name: String) = !locations.find { loc =>
    name.toUpperCase.startsWith(loc)
  }.isEmpty

  def isDND(name: String) = name.toUpperCase.contains("DO-NOT-DELETE")

  def isSparedName(name: String) = isDND(name) || isLOTR(name)
}

class RxDalek[T] extends Dalek[T] {
  var region: Region = null
  def regions: Regions = if (region != null) {
    extra += ("region" -> region.toString())
    Regions.fromName(region.toString)
  } else Regions.US_EAST_1 //TODO Regions.DEFAULT_REGION
  
  def withRegion(region: Region):this.type = {
    this.region = region
    this
  }

}