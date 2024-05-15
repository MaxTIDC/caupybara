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
    assert(getLiterals(psi1) == Set(Not(Atom("req1")), Not(Atom("req2")), Atom("ack")))
  }

  test("ReqAckTrace3VLTest01") {
    val rou: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )
    assert(evalTrilean(rou, 0, 3, psi1) == Trilean.F)
  }

  test("ReqAckTrace3VLTest02") {
    val rou: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set(),
      3 -> Set()
    )
    assert(evalTrilean(rou, 0, 3, psi1) == Trilean.U)
  }

  test("ReqAckTrace3VLTest03") {
    val rou: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set("ack")
    )
    assert(evalTrilean(rou, 0, 3, psi1) == Trilean.U)
  }

  test("ReqAckTrace3VLTest04") {
    val rou: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req2"),
      3 -> Set()
    )
    assert(evalTrilean(rou, 0, 3, psi1) == Trilean.F)
  }

  test("StartEndStatus3VLTest") {
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

    assert(evalTrilean(trace, 0, 9, psi) == Trilean.F)
    assert(evalTrilean(trace, 6, 9, psi) == Trilean.F)
    assert(evalTrilean(trace, 7, 9, psi) != Trilean.F)
    assert(evalTrilean(trace, 7, 11, psi) != Trilean.F)
  }

  test("Minepump3VLTest01") {
    val psi = And(Not(Atom("highwater")), Not(Atom("methane")))

    val rou: Trace = Map(
      0 -> Set(),
      1 -> Set("highwater", "methane")
    )

    assert(evalTrilean(rou, 0, 1, psi) == Trilean.T)
  }

}
