package pl.com.britenet.ceglarski.oskar

import org.apache.poi.ss.usermodel.{CellType, Row, WorkbookFactory}

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

case class TempNode(index: Int, id: Int, name: String, nestedLevel: Int, nodes: ListBuffer[TempNode], parentIndex: Int = 0)
case class Node(id: Int, name: String, nodes: List[Node] = List())

object Main {

  def main(args: Array[String]): Unit = {

    val ID_ROW_INDEX: Int = 3
    val FILE: String = "test1.xlsx"

    val fileAsStream = getClass.getClassLoader.getResourceAsStream(FILE)
    val workbook = WorkbookFactory.create(fileAsStream)

    val rows = workbook.getSheetAt(0).iterator().asScala.toList

    val isRowANode = (row: Row) => row.getCell(ID_ROW_INDEX).getCellType == CellType.NUMERIC
    val filteredRows = rows.filter(isRowANode)
    val tempNodesWithoutParent: List[TempNode] = filteredRows.zipWithIndex.map { case (row, index) => mapToTempNode(ID_ROW_INDEX, row, index) }
    println(tempNodesWithoutParent)

    val tempNodesWithParent: List[TempNode] = findParent(tempNodesWithoutParent)
    println(tempNodesWithParent)

    val nodes = createNodesList(tempNodesWithParent, List(), 3)
    println(nodes)
  }

  def mapToTempNode(cellNum: Int, row: Row, index: Int): TempNode = {
    val id = row.getCell(cellNum).getNumericCellValue.intValue

    val cellAndIndex = row.cellIterator().asScala.toList.zipWithIndex.find {
      case (cell, _) => cell.getCellType == CellType.STRING
    }.get

    val name = cellAndIndex._1.getStringCellValue
    val nestedLevel = cellAndIndex._2 + 1

    TempNode(index + 2, id, name, nestedLevel, ListBuffer())
  }

  def findParent(nodes: List[TempNode]): List[TempNode] = {

    var lastLevelParentDictionary = Map(1 -> 0, 2 -> 0, 3 -> 0)
    val nodesWithParent: ListBuffer[TempNode] = ListBuffer()

    nodes.foreach { node => {
      if (node.index > lastLevelParentDictionary.getOrElse(node.nestedLevel, 0)) {
        lastLevelParentDictionary = lastLevelParentDictionary + (node.nestedLevel -> node.index)
      }
      val nodeWithParent = node.copy(parentIndex = lastLevelParentDictionary.getOrElse(node.nestedLevel - 1, 0))
      nodesWithParent.addOne(nodeWithParent)
    }
    }
    nodesWithParent.toList
  }

  def createNodesList(list: List[TempNode], nodes: List[Node], level: Int): List[Node] =
    level match {
      case 0 => nodes
      case _ =>
        val parents: List[TempNode] = list.filter(node => node.nestedLevel == level)
        val children: List[TempNode] = list.filter(node => node.nestedLevel == level + 1)
        val readyNodes = parents
          .map(
            tempNode => {
              val nodeChildren = children.filter(_.parentIndex == tempNode.index).map(tempNode => tempNode.id)
              Node(id = tempNode.id, name = tempNode.name, nodes = nodes.filter(node => nodeChildren.contains(node.id)))
            })
          .filter(t => parents.filter(_.nestedLevel == level).map(tempNode => tempNode.id).contains(t.id))
        createNodesList(list, readyNodes, level - 1)

    }

  def sortByIdDesc(node1: TempNode, node2: TempNode): Boolean = {
    node1.id > node2.id
  }

}
