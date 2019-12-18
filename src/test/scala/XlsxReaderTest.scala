import org.scalatest.funsuite.AnyFunSuite
import pl.com.britenet.ceglarski.oskar.XlsxReader

class XlsxReaderTest extends AnyFunSuite {
  test("should return number of nodes rows") {

    //given
    val givenTempNodesList = "test1.xlsx"
    val index_row_index = 3

    //when
    val readNodesRows = XlsxReader.getNodesRows(givenTempNodesList, index_row_index)

    //then
    assert(readNodesRows.length == 12)
  }
}
