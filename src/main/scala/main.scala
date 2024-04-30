import CauseBeer2011.causeApprox
import Lib.*
import Util.*

@main
def main(): Unit = {
  val psi1 = toNNF(LTLParser("G((!req1 & !req2) | X ack)"))

  val rou1: Trace = Map(
    0 -> Set("req1"),
    1 -> Set("ack"),
    2 -> Set("req1", "req2"),
    3 -> Set()
  )

  println(evalTrilean(rou1, 2, 3, psi1))
}