import CauseBeer2011.causeApprox
import Lib.*
import Util.*

@main
def main(): Unit = {
  val psi1 = toNNF(G(Implies(Or(Atom("req1"), Atom("req2")), X(Atom("ack")))))

  val rou1: Trace = Map(
    0 -> Set("req1"),
    1 -> Set("ack"),
    2 -> Set("req1", "req2"),
    3 -> Set()
  )

  val C = causeApprox(rou1, 0, psi1)

  println(C)
}