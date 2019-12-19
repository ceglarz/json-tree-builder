package pl.com.britenet.ceglarski.oskar

import scala.collection.mutable.ListBuffer

case class TempNode(index: Int, id: Int, name: String, nestedLevel: Int, nodes: ListBuffer[TempNode], parentIndex: Int = -1)
case class Node(id: Int, name: String, nodes: List[Node] = List())