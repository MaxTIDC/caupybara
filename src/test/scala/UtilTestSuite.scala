import Lib.*
import Util.*
import org.scalatest.funsuite.AnyFunSuite

class UtilTestSuite extends AnyFunSuite {
  // Trace file parse tests
  test("ReqAckTraceParseTest01") {
    val actualTrace = parseTraceFromPath("input-files/Beer2011/req_ack_violation_1.txt")
    val expectedTrace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )
    assert(actualTrace == expectedTrace)
  }

  test("ReqAckTraceParseTest02") {
    val actualTrace = parseTraceFromPath("input-files/Beer2011/req_ack_violation_2.txt")
    val expectedTrace = Map(
      0 -> Set("req1"),
      1 -> Set("req1", "ack"),
      2 -> Set(),
      3 -> Set()
    )
    assert(actualTrace == expectedTrace)
  }

  test("StartEndStatusParseTest01") {
    val actualTrace = parseTraceFromPath("input-files/Beer2011/start_end_status_violation.txt")
    val expectedTrace = Map(
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
    assert(actualTrace == expectedTrace)
  }

  test("TrafficParseTest01") {
    val actualTrace = parseTraceFromPath("input-files/traffic.txt")
    val expectedTrace = Map(
      0 -> Set(),
      1 -> Set("carA", "emergency"),
      2 -> Set(),
    )
    assert(actualTrace == expectedTrace)
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
    val actualPhi = LTLParser("!req1 & !req2")
    val expectedPhi = And(Not(Atom("req1")), Not(Atom("req2")))
    assert(actualPhi == expectedPhi)
  }

  test("ReqAckLTLParseTest01") {
    val actualPhi = LTLParser("G((!req1 & !req2) | X ack)")
    val expectedPhi = G(Or(And(Not(Atom("req1")), Not(Atom("req2"))), X(Atom("ack"))))
    assert(actualPhi == expectedPhi)
  }

  test("ReqAckLTLParseTest03") {
    assert(LTLParser("GF!req1") == G(F(Not(Atom("req1")))))
  }

  test("ArbiterParseTest01") {  // TODO: improve parser to support ambiguous bracketing
    assert(LTLParser("!g1&!g2") == And(Not(Atom("g1")), Not(Atom("g2"))))
//    assert(LTLParser("!a->!g1&!g2") == Implies(Not(Atom("a")), And(Not(Atom("g1")), Not(Atom("g2")))))
//    assert(LTLParser("G(!a->!g1&!g2)") == G(Implies(Not(Atom("a")), And(Not(Atom("g1")), Not(Atom("g2"))))))
  }

  test("ArbiterParseTest02") {
    assert(LTLParser("G(!a -> next(a))") == G(Implies(Not(Atom("a")), X(Atom("a")))))
  }

  test("TrafficParseTests") { // TODO: improve parser to support ambiguous bracketing
    assert(LTLParser("alwEv carA") == G(F(Atom("carA"))))
    assert(LTLParser("alw (carA & !greenA) -> next(carA)") == G(Implies(And(Atom("carA"), Not(Atom("greenA"))), X(Atom("carA")))))
    //    assert(LTLParser("alw carA & !greenA -> next(carA)") == G(Implies(And(Atom("carA"), Not(Atom("greenA"))), X(Atom("carA")))))
  }

  // Fault localizer tests
  test("ReqAckFaultLocalizerTest01") {
    val trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val psi =
      G(
        Or(
          And(
            Not(Atom("req1")),
            Not(Atom("req2"))
          ),
          X(Atom("ack"))
        )
      )

    assert(localizeFaults(trace, psi) == Set((2, 3)))
  }

  test("ReqAckFaultLocalizerTest02") {
    val trace = Map(
      0 -> Set("req1"),
      1 -> Set("req1", "ack"),
      2 -> Set(),
      3 -> Set()
    )

    val psi =
      G(
        Or(
          And(
            Not(Atom("req1")),
            Not(Atom("req2"))
          ),
          X(Atom("ack"))
        )
      )

    assert(localizeFaults(trace, psi) == Set((1, 2)))
  }

  test("StartEndStatusFaultLocalizerTest") {
    val psi =
      G(
        Or(
          Or(
            Or(
              Atom("start"),
              Atom("status_valid")
            ),
            Not(Atom("end")),
          ),
          U(
            Not(Atom("start")),
            Atom("status_valid")
          )
        )
      )

    val trace: Trace = Map(
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

    assert(localizeFaults(trace, psi) == Set((6, 9)))
  }
}
