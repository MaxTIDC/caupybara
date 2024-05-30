import CauseBeer2011.causeApprox
import Lib.*
import org.scalatest.funsuite.AnyFunSuite

class CauseBeer2011TestSuite extends AnyFunSuite {
  test("ReqAckTest01") {
    val psi1 =
      G(
        Or(
          And(
            Not(Atom("req1")),
            Not(Atom("req2"))
          ),
          X(Atom("ack"))
        )
      )

    val rou1: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val C = causeApprox(rou1, 0, psi1)

    assert(C == Set((2, "req1"), (2, "req2"), (3, "ack")))
  }

  test("ReqAckTest02") {
    val psi1 =
      G(
        Or(
          And(
            Not(Atom("req1")),
            Not(Atom("req2"))
          ),
          X(Atom("ack"))
        )
      )

    val rou1: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("req1", "ack"),
      2 -> Set(),
      3 -> Set()
    )

    val C = causeApprox(rou1, 0, psi1)

    assert(C == Set((1, "req1"), (2, "ack")))
  }

  // Fig. 8 in Beer et al. 2011
  test("ReqAckTest03") {
    val psi = U(Atom("req"), Atom("ack"))

    val trace: Trace = Map(
      0 -> Set("req"),
      1 -> Set("req"),
      2 -> Set(),
      3 -> Set(),
      4 -> Set(),
      5 -> Set(),
      6 -> Set(),
    )

    val actualCauses = Set(
      (0, "ack"),
      (1, "ack"),
      (2, "req"),
      (2, "ack"),
    )

    assert(causeApprox(trace, 0, psi) == actualCauses)
  }

  test("StartEndStatusTest") {
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

    val rou: Trace = Map(
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

    val C = causeApprox(rou, 0, psi)

    assert(C == Set((6, "start"), (6, "end"), (6, "status_valid"),
      (7, "status_valid"), (8, "status_valid"), (9, "start"), (9, "status_valid")))
  }

  test("MinepumpTest01") {
    val psi = G(Or(X(Not(Atom("highwater"))), Y(Not(Atom("pump")))))

    val rou: Trace = Map(
      0 -> Set("pump"),
      1 -> Set(),
      2 -> Set("highwater")
    )

    assert(causeApprox(rou, 0, psi) == Set((0, "pump"), (2, "highwater")))
  }
}