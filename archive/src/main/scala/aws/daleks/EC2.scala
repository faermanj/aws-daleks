package aws.daleks

object EC2 {
  val sgs = scala.collection.mutable.SortedSet[String]()
  

  def setMercyOnSG(id: String) = this.synchronized {
    sgs += id
  }

  def isMercyOnSG(id: String) = this.synchronized {
    sgs.contains(id)
  }

}