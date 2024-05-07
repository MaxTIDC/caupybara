import CauseHerong2024.*
import CauseHerong2024.Cause.*
import Lib.*
import Util.toNNF
import org.scalatest.funsuite.AnyFunSuite

class CauseHerong2024TestSuite extends AnyFunSuite {
  test("FlipAtomsTest01") {
    val trace: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val cause: CausalSet = Set((2, "req1", true), (2, "req2", true))

    val newTrace: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set(),
      3 -> Set()
    )

    assert(flipAtomsInTrace(trace, cause) == newTrace)
  }

  test("FlipAtomsTest02") {
    val trace: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val cause: CausalSet = Set((3, "ack", false))

    val newTrace: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set("ack")
    )

    assert(flipAtomsInTrace(trace, cause) == newTrace)
  }

  test("GetMaximalCausalSetTest01") {
    val trace: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val propAtoms = Set("req1", "req2", "ack")

    val maximalCS = Set(
      (2, "req1", true), (2, "req2", true), (2, "ack", false),
      (3, "req1", false), (3, "req2", false), (3, "ack", false)
    )

    assert(getMaximalCausalSet(trace, 2, 3, propAtoms) == maximalCS)
  }

  test("MinCritSetsOfSizeTest01") {
    val trace: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val psi = toNNF(
      G(
        Or(
          And(
            Not(Atom("req1")),
            Not(Atom("req2"))
          ),
          X(Atom("ack"))
        )
      ))

    val propAtoms = Set("req1", "req2", "ack")

    // Only cause of size 1 is {<3, !ack>}
    assert(findMinCritSetsOfSize(trace, 2, 3, psi, propAtoms, 1) == Set(Set((3, "ack", false))))
    // Only cause of size 2 is {<2, req1>, <2, req2>}
    assert(findMinCritSetsOfSize(trace, 2, 3, psi, propAtoms - "ack", 2) == Set(Set((2, "req1", true), (2, "req2", true))))
  }
}