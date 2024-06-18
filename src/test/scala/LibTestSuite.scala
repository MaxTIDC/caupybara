import Lib.*
import Lib.EvalFionda2016.eval
import Util.NNFConverter
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

  // Fionda tests
  test("ReqAckTraceFiondaTest01") {
    val rou: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set()
    )
    assert(!eval(rou, 0, 3, psi1))
  }

  test("ReqAckTraceFiondaTest02") {
    val rou: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set(),
      3 -> Set()
    )
    assert(eval(rou, 0, 3, psi1))
  }

  test("ReqAckTraceFiondaTest03") {
    val rou: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req1", "req2"),
      3 -> Set("ack")
    )
    assert(eval(rou, 0, 3, psi1))
  }

  test("ReqAckTraceFiondaTest04") {
    val rou: Execution = Map(
      0 -> Set("req1"),
      1 -> Set("ack"),
      2 -> Set("req2"),
      3 -> Set()
    )
    assert(!eval(rou, 0, 3, psi1))
  }

  test("StartEndStatusFiondaTest") {
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

    assert(!eval(trace, 0, 9, psi))
    assert(!eval(trace, 6, 9, psi))
    assert(eval(trace, 7, 9, psi))
    assert(eval(trace, 7, 11, psi))
  }

  test("MinepumpFiondaTest01") {
    val psi = And(Not(Atom("highwater")), Not(Atom("methane")))

    val rou: Execution = Map(
      0 -> Set(),
      1 -> Set("highwater", "methane")
    )

    assert(eval(rou, 0, 1, psi))
  }

  test("MinepumpFiondaTest02") {
    val psi = G(Or(X(Not(Atom("highwater"))), Y(Not(Atom("pump")))))

    val rou1: Execution = Map(
      0 -> Set("pump"),
      1 -> Set(),
      2 -> Set("highwater")
    )
    val rou2: Execution = Map(
      0 -> Set("pump"),
      1 -> Set(),
      2 -> Set()
    )
    val rou3: Execution = Map(
      0 -> Set(),
      1 -> Set(),
      2 -> Set("highwater")
    )
    val rou4: Execution = Map(
      0 -> Set("pump"),
      1 -> Set("highwater"),
      2 -> Set()
    )

    assert(!eval(rou1, 0, 2, psi))
    assert(!eval(rou4, 0, 2, psi))

    assert(eval(rou2, 0, 2, psi))
    assert(eval(rou3, 0, 2, psi))
  }

  test("MinepumpFiondaTest03") {
    val phi =
      G(
        Or(
          X(Not(Atom("highwater"))),
          Not(Atom("pump"))
        )
      )

    val trace1: Execution = Map(
      0 -> Set("pump", "highwater"),
      1 -> Set("pump", "highwater"),
    )
    val trace2: Execution = Map(
      0 -> Set("highwater"),
      1 -> Set("pump", "highwater"),
    )

    assert(!eval(trace1, 0, 1, phi))
    assert(eval(trace2, 0, 1, phi))
  }

  test("FiondaPaperTest") {
    val phi =
      And(
        And(Atom("a"), Not(Atom("b"))),
        And(
          F(
            And(
              Atom("c"),
              G(Atom("a"))
            )
          ),
          X(Atom("b"))
        )
      )

    val trace: Execution = Map(
      0 -> Set("a"),
      1 -> Set("b"),
      2 -> Set("a", "c"),
      3 -> Set("a"),
      4 -> Set("a", "c"),
      5 -> Set("a"),
      6 -> Set("a"),
    )

    assert(eval(trace, 0, 6, phi))
  }

  test("ArbiterFiondaTest01") {
    val phi = G(Atom("a"))
    val trace: Execution = Map(
      0 -> Set(),
    )

    assert(!eval(trace, 0, 0, phi))
  }

  test("ArbiterFiondaTest02") {
    val phi =
      G(
        Implies(
          Atom("r1"),
          F(Atom("g1"))
        )
      )

    val trace: Execution = Map(
      0 -> Set("r1"),
    )

    assert(!eval(trace, 0, 0, phi))
    assert(!eval(trace, 0, 0, NNFConverter.toNNF(phi)))
  }

  test("ArbiterFiondaTest03") {
    val phi =
      G(
        Or(
          Not(Not(Atom("a"))),
          And(
            Not(Atom("g1")),
            Not(Atom("g2"))
          )
        )
      )

    val trace: Execution = Map(
      0 -> Set("a", "g1", "r1", "r2"),
      1 -> Set(),
    )

    assert(eval(trace, 0, 1, NNFConverter.toNNF(phi)))
  }

  test("TrafficSingleFiondaTest01") {
    val phi =
      G(
        F(
          Not(Atom("car"))
        )
      )

    val trace0: Execution = Map(
      0 -> Set("car", "green", "police"),
      1 -> Set("car"),
    )
    val trace1: Execution = Map(
      0 -> Set("green", "police"),
      1 -> Set("car"),
    )
    val trace2: Execution = Map(
      0 -> Set("car", "green", "police"),
      1 -> Set(),
    )

    assert(!eval(trace0, 0, 1, NNFConverter.toNNF(phi)))
    assert(!eval(trace1, 0, 1, NNFConverter.toNNF(phi)))
    assert(eval(trace2, 0, 1, NNFConverter.toNNF(phi)))
  }
}
