import Lib.*
import Util.*
import org.scalatest.funsuite.AnyFunSuite

class UtilTestSuite extends AnyFunSuite {
  // Trace file parse tests
  test("ReqAckTraceParseTest01") {
    val actualTrace = parseMultiTracesFromPath("input-files/custom/req_ack_violation_1.txt")
    val expectedTrace = Map(
      "trace_name_0" -> Map(
        0 -> Set("req1"),
        1 -> Set("ack"),
        2 -> Set("req1", "req2"),
        3 -> Set()
      )
    )
    assert(actualTrace == expectedTrace)
  }

  test("ReqAckTraceParseTest02") {
    val actualTrace = parseMultiTracesFromPath("input-files/custom/req_ack_violation_2.txt")
    val expectedTrace = Map(
      "trace_name_0" -> Map(
        0 -> Set("req1"),
        1 -> Set("req1", "ack"),
        2 -> Set(),
        3 -> Set()
      )
    )
    assert(actualTrace == expectedTrace)
  }

  test("StartEndStatusParseTest01") {
    val actualTrace = parseMultiTracesFromPath("input-files/custom/start_end_status_violation.txt")
    val expectedTrace = Map(
      "trace_name_0" -> Map(
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
    )
    assert(actualTrace == expectedTrace)
  }

  test("TrafficParseTest01") {
    val actualTrace = parseMultiTracesFromPath("input-files/custom/traffic.txt")
    val expectedTrace = Map(
      "violation_trace" -> Map(
        0 -> Set(),
        1 -> Set("carA", "emergency"),
        2 -> Set(),
      )
    )
    assert(actualTrace == expectedTrace)
  }

  test("LiftMultiTraceParseTest01") {
    val actual = parseMultiTracesFromPath("input-files/buckworth2023/violation_files/lift_well_sep_dropped0_auto_violation.txt")
    val expected = Map(
      "trace_name_0" -> Map(
        0 -> Set("c", "f1"),
        1 -> Set("b1", "f1"),
      ),
      "trace_name_1" -> Map(
        0 -> Set("f1"),
        1 -> Set("b1", "f1"),
      ),
    )
  }

  // NNF tests
  test("ReqAckToNNFTest") {
    val actualPhi = toNNF(G(Implies(Or(Atom("req1"), Atom("req2")), X(Atom("ack")))))
    val expectedPhi =
      G(
        Or(
          And(
            Not(Atom("req1")),
            Not(Atom("req2"))
          ),
          X(Atom("ack"))
        )
      )
    assert(actualPhi == expectedPhi)
  }

  test("StartEndStatusToNNFTest") {
    val actualPhi = toNNF(
      G(
        Implies(
          And(
            And(Not(Atom("start")), Not(Atom("status_valid"))),
            Atom("end")
          ),
          U(Not(Atom("start")), Atom("status_valid")))
      )
    )
    val expectedPhi =
      G(
        Or(
          Or(
            Or(Atom("start"), Atom("status_valid")),
            Not(Atom("end")),
          ),
          U(Not(Atom("start")), Atom("status_valid"))
        )
      )
    assert(actualPhi == expectedPhi)
  }

  test("P1P2ActiveToNNFTest") {
    val actualPhi = toNNF(
      G(
        Implies(
          Atom("p1_active"),
          F(Atom("p2_active"))
        )
      )
    )
    val expectedPhi =
      G(
        Or(
          Not(Atom("p1_active")),
          U(True, Atom("p2_active"))
        )
      )
    assert(actualPhi == expectedPhi)
  }

  // LTL parse tests
  test("PLParseTest01") {
    assert(LTLParser("!req1 & !req2") == And(Not(Atom("req1")), Not(Atom("req2"))))
  }

  test("ReqAckLTLParseTest01") {
    assert(
      LTLParser("G((!req1 & !req2) | X ack)")
        == G(Or(And(Not(Atom("req1")), Not(Atom("req2"))), X(Atom("ack"))))
    )
  }

  test("ReqAckLTLParseTest03") {
    assert(LTLParser("GF!req1") == G(F(Not(Atom("req1")))))
  }

  test("ArbiterParseTest01") {
    assert(
      LTLParser("G(!a->!g1&!g2)")
        == G(Implies(Not(Atom("a")), And(Not(Atom("g1")), Not(Atom("g2")))))
    )
  }

  test("ArbiterParseTest02") {
    assert(LTLParser("G(!a -> next(a))") == G(Implies(Not(Atom("a")), X(Atom("a")))))
  }

  test("TrafficParseTests01") {
    assert(
      LTLParser("alw carA & !greenA -> next(carA)")
        == G(Implies(And(Atom("carA"), Not(Atom("greenA"))), X(Atom("carA"))))
    )
  }

  test("TrafficParseTests02") {
    assert(
      LTLParser("alw carA & !greenA -> next(carA)")
        == G(Implies(And(Atom("carA"), Not(Atom("greenA"))), X(Atom("carA"))))
    )
  }

  test("TrafficParseTests03") {
    assert(
      LTLParser("ini !carA & !carB & !emergency")
        == And(And(Not(Atom("carA")), Not(Atom("carB"))), Not(Atom("emergency")))
    )
  }

  test("TrafficParseTests04") {
    assert(
      LTLParser("ini !carA & !carB | !emergency")
        == Or(And(Not(Atom("carA")), Not(Atom("carB"))), Not(Atom("emergency")))
    )
  }

  test("MinepumpParseTests01") {
    assert(
      LTLParser("G(next(!highwater) | PREV(!pump))")
        == G(Or(X(Not(Atom("highwater"))), Y(Not(Atom("pump")))))
    )
  }
}
