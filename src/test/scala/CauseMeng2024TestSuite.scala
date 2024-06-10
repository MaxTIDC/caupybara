import CauseMeng2024.*
import CauseMeng2024.Cause.*
import Lib.*
import Util.toNNF
import org.scalatest.funsuite.AnyFunSuite

class CauseMeng2024TestSuite extends AnyFunSuite {
  // Component unit tests
  test("FlipAtomsTest01") {
    val trace: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val cause: CausalSet = Set((2, "req1", true), (2, "req2", true))

    val newTrace: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set(),
      3 -> Set()
    )

    assert(flipAtomsInTrace(trace, cause) == newTrace)
  }

  test("FlipAtomsTest02") {
    val trace: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val cause: CausalSet = Set((3, "ack", false))

    val newTrace: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set("ack")
    )

    assert(flipAtomsInTrace(trace, cause) == newTrace)
  }

  test("GetNegativeLiteralAtomsTest01") {
    val trace: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val literals = Set(Not(Atom("req1")), Not(Atom("req2")), Atom("ack"))

    val expectedCS = Set(
      (2, "req1", true), (2, "req2", true), (2, "ack", false),
      (3, "ack", false),
    )

    assert(getNegativeLiteralsAtoms(trace, 2, 3, literals) == expectedCS)
  }

  test("GetNegativeLiteralAtomsTest02") {
    val trace: Execution = Map(
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

    val literals = Set(Atom("start"), Atom("status_valid"), Not(Atom("end")), Not(Atom("start")))

    val expectedCSPartial = Set(
      (6, "start", false), (6, "status_valid", false), (6, "end", true),
      (7, "start", false), (7, "status_valid", false),
      (8, "start", false), (8, "status_valid", false),
      (9, "start", true), (9, "status_valid", false),
    )

    val expectedCSFull = Set(
      (0, "start", false), (0, "status_valid", false),
      (1, "start", true), (1, "status_valid", false),
      (2, "start", false), (2, "status_valid", false),
      (3, "start", false), (3, "status_valid", false), (3, "end", true),
      (4, "start", true),
      (5, "start", false), (5, "status_valid", false),
      (6, "start", false), (6, "status_valid", false), (6, "end", true),
      (7, "start", false), (7, "status_valid", false),
      (8, "start", false), (8, "status_valid", false),
      (9, "start", true), (9, "status_valid", false),
      (10, "start", false),
      (11, "start", false), (11, "status_valid", false),
    )

    assert(getNegativeLiteralsAtoms(trace, 6, 9, literals) == expectedCSPartial)
    assert(getNegativeLiteralsAtoms(trace, 0, 11, literals) == expectedCSFull)
  }

  test("SubsetsOfSizeTest") { // Sanity check
    assert(Set(1, 2, 3, 4).subsets(2) sameElements
      Iterator(Set(1, 2), Set(1, 3), Set(1, 4), Set(2, 3), Set(2, 4), Set(3, 4))
    )
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

    val trace: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )

    val expectedCauses = Set(
      Set((3, "ack", false)),
      Set((2, "req1", true), (2, "req2", true))
    )

    assert(findViolationCauses(trace, 0, 3, psi) == expectedCauses)
//    Cause.reset()
  }

  test("ReqAckCausalityTest02") {
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

    val trace: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("req1", "ack"),
      2 -> Set(),
      3 -> Set()
    )

    val expectedCauses = Set(
      Set((1, "req1", true)),
      Set((2, "ack", false))
    )

    assert(findViolationCauses(trace, 0, 3, psi) == expectedCauses)
//    Cause.reset()
  }

  /** Fig. 8 in Beer et al. 2011 */
  test("ReqAckCausalityTest03") {
    val psi = U(Atom("req"), Atom("ack"))

    val trace: Execution = Map(
      0 -> Set("req"),
      1 -> Set("req"),
      2 -> Set(),
      3 -> Set(),
      4 -> Set(),
      5 -> Set(),
      6 -> Set(),
    )

    val expectedCauses = Set(
      Set((0, "ack", false)),
      Set((1, "ack", false)),
      Set((2, "ack", false)),
      Set((2, "req", false), (3, "ack", false))
    )

    assert(findViolationCauses(trace, 0, 6, psi) == expectedCauses)
//    Cause.reset()
  }

  test("StartEndStatusCausalityTest") {
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

    val trace: Execution = Map(
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

    val expectedCauses = Set(
      Set((6, "start", false)),
      Set((6, "end", true)),
      Set((6, "status_valid", false)),
      Set((7, "status_valid", false)),
      Set((8, "status_valid", false)),
      Set((9, "start", true)),
      Set((9, "status_valid", false))
    )

    assert(findViolationCauses(trace, 0, 11, psi) == expectedCauses) // led to blowup
//    Cause.reset()
  }

  test("MinepumpTest01") {
    val psi = G(Or(X(Not(Atom("highwater"))), Y(Not(Atom("pump")))))

    val rou: Execution = Map(
      0 -> Set("pump"),
      1 -> Set(),
      2 -> Set("highwater")
    )

    val expectedCauses = Set(Set((0, "pump", true)), Set((2, "highwater", true)))

    assert(findViolationCauses(rou, 0, 2, psi) == expectedCauses)
  }
}