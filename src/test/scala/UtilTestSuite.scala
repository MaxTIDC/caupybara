import Lib.*
import Util.*
import org.scalatest.funsuite.AnyFunSuite

class UtilTestSuite extends AnyFunSuite {
  test("ReqAckTraceParseTest01") {
    val parsedTrace = parseTraceFromPath("input-files/Beer2011/req_ack_violation_1.txt")
    val actualTrace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )
    assert(parsedTrace == actualTrace)
  }

  test("ReqAckTraceParseTest02") {
    val parsedTrace = parseTraceFromPath("input-files/Beer2011/req_ack_violation_2.txt")
    val actualTrace = Map(
      0 -> Set("req1"),
      1 -> Set("req1", "ack"),
      2 -> Set(),
      3 -> Set()
    )
    assert(parsedTrace == actualTrace)
  }

  test("StartEndStatusParseTest01") {
    val parsedTrace = parseTraceFromPath("input-files/Beer2011/start_end_status_violation.txt")
    val actualTrace = Map(
      0 -> Set(),
      1 -> Set("start"),
      2 -> Set(),
      3 -> Set("end"),
      4 -> Set("start", "status_valid"),
      5 -> Set(),
      6 -> Set("end"),
      7 -> Set(),
      8 -> Set(),
      9 -> Set("start"),
      10 -> Set("status_valid"),
      11 -> Set(),
    )
    assert(parsedTrace == actualTrace)
  }

  test("PLParseTest01") {
    val parsedPhi = LTLParser("!req1 & !req2")
    val actualPhi = And(Not(Atom("req1")), Not(Atom("req2")))
    assert(parsedPhi == actualPhi)
  }

  test("ReqAckLTLParseTest01") {
    val parsedPhi = LTLParser("G((!req1 & !req2) | X(ack))")
    val actualPhi = G(Or(And(Not(Atom("req1")), Not(Atom("req2"))), X(Atom("ack"))))
    assert(parsedPhi == actualPhi)
  }
}
