package aws.daleks.util

import java.util.concurrent.atomic.AtomicBoolean

sealed trait Humid[+A] extends Traversable[A] {
  def flatMap[B](f: A => Humid[B]): Humid[B]
  def get:A
  def isEmpty:Boolean
  
  override def foreach[U](f: A => U): Unit =
      if (! isEmpty) f(get)
}

case class Wet[+A](a: A) extends Humid[A] {
  override def flatMap[B](f: A => Humid[B]): Humid[B] = f(a)
  override def get:A = a
  override def isEmpty:Boolean = false
}

case object Dry extends Humid[Nothing] {
  override def flatMap[B](f: Nothing => Humid[B]): Humid[B] = Dry
  override def isEmpty = true
  override def get = throw new NoSuchElementException("Dry.get")
}

object Humid {
  val globalDry = new AtomicBoolean(true) 
  def isDry = globalDry.get
  def apply[A](x:A):Humid[A] = if (isDry) Dry else Wet(x)
}
