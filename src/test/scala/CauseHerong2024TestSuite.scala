import CauseHerong2024.*
import CauseHerong2024.Cause.*
import Lib.*
import Util.toNNF
import org.scalatest.funsuite.AnyFunSuite

class CauseHerong2024TestSuite extends AnyFunSuite {
  // Component unit tests
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

    assert(getFullAtomVals(trace, 2, 3, propAtoms) == maximalCS)
  }

  // Tests for cause computation
  test("ReqAckCausalityTest01") {
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

    val trace: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val actualCauses = Set(
      Set((3, "ack", false)),
      Set((2, "req1", true), (2, "req2", true))
    )

    assert(findViolationCauses(trace, 0, 3, psi) == actualCauses)
  }

  test("ReqAckTest02") {
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

    val trace: Trace = Map(
      0 -> Set("req1"),
      1 -> Set("req1", "ack"),
      2 -> Set(),
      3 -> Set()
    )

    val actualCauses = Set(
      Set((1, "req1", true)),
      Set((2, "ack", false))
    )

    assert(findViolationCauses(trace, 0, 3, psi) == actualCauses)
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

    val actualCauses = Set(
      Set((6, "start", false)),
      Set((6, "end", true)),
      Set((6, "status_valid", false)),
      Set((7, "status_valid", false)),
      Set((8, "status_valid", false)),
      Set((9, "start", true)),
      Set((9, "status_valid", false))
    )

//    val actualCauses = Set(
//      Set((6, "start", false), (6, "end", true), (6, "status_valid", false),
//        (7, "status_valid", false),
//        (8, "status_valid", false),
//        (9, "start", true), (9, "status_valid", false))
//    )

    assert(findViolationCauses(trace, 6, 9, psi) == actualCauses)  // i=0, k=11 led to blowup
  }

//  test("MinCritSetsOfSizeTest01") {
//    val trace: Trace = Map(
//      0 -> Set("req1"),
//      1 -> Set("ack"),
//      2 -> Set("req1", "req2"),
//      3 -> Set()
//    )
//
//    val psi = toNNF(
//      G(
//        Or(
//          And(
//            Not(Atom("req1")),
//            Not(Atom("req2"))
//          ),
//          X(Atom("ack"))
//        )
//      ))
//
//    val propAtoms = Set("req1", "req2", "ack")
//
//    // Only cause of size 1 is {<3, !ack>}
//    assert(findMinCritSetsOfSize(trace, 2, 3, psi, propAtoms, 1) == Set(Set((3, "ack", false))))
//    // Only cause of size 2 is {<2, req1>, <2, req2>}
//    assert(findMinCritSetsOfSize(trace, 2, 3, psi, propAtoms - "ack", 2) == Set(Set((2, "req1", true), (2, "req2", true))))
//  }
}