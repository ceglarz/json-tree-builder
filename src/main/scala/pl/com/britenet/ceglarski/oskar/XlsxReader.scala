package pl.com.britenet.ceglarski.oskar

import org.apache.poi.ss.usermodel.{CellType, Row, WorkbookFactory}

import scala.jdk.CollectionConverters._

object XlsxReader {

  def getNodesRows(filePath: String, idRow: Int): List[Row] = {


    try {
      val fileAsStream = getClass.getClassLoader.getResourceAsStream(filePath)
      val workbook = WorkbookFactory.create(fileAsStream)
      val rows = workbook.getSheetAt(0).iterator().asScala.toList

      filteredRows(rows, idRow)
    } catch {
      case ex: NullPointerException => {
        println("Wrong file structure")
        List[Row]()
      }
    }
  }

  private def filteredRows(rows: List[Row], idRow: Int) : List[Row] = {

    val isRowANode = (row: Row) => row.getCell(idRow).getCellType == CellType.NUMERIC
    rows.filter(isRowANode)
  }
}
