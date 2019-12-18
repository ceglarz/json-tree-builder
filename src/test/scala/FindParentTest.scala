import org.scalatest.funsuite.AnyFunSuite
import pl.com.britenet.ceglarski.oskar.{Main, TempNode}

import scala.collection.mutable.ListBuffer

class FindParentTest extends AnyFunSuite {
  test("should return nodes list with parent index") {

    //given
    val givenTempNodesList = List(
        TempNode(0, 1, "A", 1, ListBuffer(), -1),
        TempNode(1, 2, "AA", 2, ListBuffer(), -1),
        TempNode(2, 3, "AA1", 3, ListBuffer(), -1)
      )

    //when
    val returnTempNodesList = Main.findParent(givenTempNodesList, 3)

    val expectedTempNodesList = List(
      TempNode(0, 1, "A", 1, ListBuffer(), -1),
      TempNode(1, 2, "AA", 2, ListBuffer(), 0),
      TempNode(2, 3, "AA1", 3, ListBuffer(), 1)
    )

    //then
    assert(returnTempNodesList === expectedTempNodesList)
  }
}
