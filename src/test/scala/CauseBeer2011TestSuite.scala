import CauseBeer2011.causeApprox
import Lib.*
import org.scalatest.funsuite.AnyFunSuite

class CauseBeer2011TestSuite extends AnyFunSuite {
  test("ReqAckTest") {
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
}