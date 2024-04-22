import Util.parseTraceFromPath
import org.scalatest.funsuite.AnyFunSuite

class UtilTestSuite extends AnyFunSuite {
  test("ReqAckParseTest01") {
    val parsedTrace = parseTraceFromPath("input-files/Beer2011/req_ack_violation_1.txt")
    val actualTrace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )
    assert(parsedTrace == actualTrace)
  }

  test("ReqAckParseTest02") {
    val parsedTrace = parseTraceFromPath("input-files/Beer2011/req_ack_violation_2.txt")
    val actualTrace = Map(
      0 -> Set("req1"),
      1 -> Set("req1", "ack"),
      2 -> Set(),
      3 -> Set()
    )
    assert(parsedTrace == actualTrace)
  }
}
