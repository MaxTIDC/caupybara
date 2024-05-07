import Lib.*
import org.scalatest.funsuite.AnyFunSuite

class LibTestSuite extends AnyFunSuite {
  private val psi1: LTL =
    G(
      Or(
        And(
          Not(Atom("req1")),
          Not(Atom("req2"))
        ),
        X(Atom("ack"))
      )
    )

  test("GetAtomsFromLTLTest01") {
    assert(getAtoms(psi1) == Set("req1", "req2", "ack"))
  }

  test("ReqAckTraceParseTest01") {
    val rou: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )
    assert(evalTrilean(rou, 0, 3, psi1) == Trilean.F)
  }

  test("ReqAckTraceParseTest02") {
    val rou: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set(),
      3 -> Set()
    )
    assert(evalTrilean(rou, 0, 3, psi1) == Trilean.U)
  }

  test("ReqAckTraceParseTest03") {
    val rou: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set("ack")
    )
    assert(evalTrilean(rou, 0, 3, psi1) == Trilean.U)
  }

  test("ReqAckTraceParseTest04") {
    val rou: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req2"),
      3 -> Set()
    )
    assert(evalTrilean(rou, 0, 3, psi1) == Trilean.F)
  }
}
