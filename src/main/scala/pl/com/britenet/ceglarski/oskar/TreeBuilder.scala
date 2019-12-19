package pl.com.britenet.ceglarski.oskar

import org.apache.poi.ss.usermodel.{CellType, Row}

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

object TreeBuilder {

  def build(): List[Node] = {

    object Builder {
      val ID_ROW_INDEX: Int = 3
      val FILE: String = "test1.xlsx"
      val NESTED_LEVELS_COUNT: Int = 3
    }

    val rows = XlsxReader.getNodesRows(Builder.FILE, Builder.ID_ROW_INDEX)

    val tempNodesWithoutParent: List[TempNode] = rows.zipWithIndex.map { case (row, index) => mapToTempNode(Builder.ID_ROW_INDEX, row, index) }
    val tempNodesWithParent: List[TempNode] = findParent(tempNodesWithoutParent, Builder.NESTED_LEVELS_COUNT)

    createNodesList(tempNodesWithParent, List(), Builder.NESTED_LEVELS_COUNT)

  }

  def mapToTempNode(cellNum: Int, row: Row, index: Int): TempNode = {
    val id = row.getCell(cellNum).getNumericCellValue.intValue

    val cellAndIndex = row.cellIterator().asScala.toList.zipWithIndex.find {
      case (cell, _) => cell.getCellType == CellType.STRING
    }.get

    val name = cellAndIndex._1.getStringCellValue
    val nestedLevel = cellAndIndex._2 + 1

    TempNode(index, id, name, nestedLevel, ListBuffer())
  }

  def findParent(nodes: List[TempNode], levels: Int): List[TempNode] = {

    var lastLevelParentDictionary = Map(0 -> -1)
    for (i <- 1 to levels) {
      lastLevelParentDictionary = lastLevelParentDictionary + (i -> -1)
    }

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
          .filter(t => parents.filter(_.nestedLevel <= level).map(tempNode => tempNode.id).contains(t.id))
        createNodesList(list, readyNodes, level - 1)

    }

}
