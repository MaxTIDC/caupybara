import lib.*
import causeBeer2011.causeApprox

@main
def main(): Unit = {
//  val psi1 = G(Implies(Or(Atom("req1"), Atom("req2")), X(Atom("ack"))))
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

  val AP: AtomicProps = Set("req1", "req2", "ack")

  val rou1: Trace = Map(
    0 -> Set("req1"),
    1 -> Set("ack"),
    2 -> Set("req1", "req2"),
    3 -> Set()
  )

  val C = causeApprox(rou1, 0, psi1)

  println(C)

//  // Create an example propositional formula: (A AND NOT B)
//  val exampleProp = And(Var("A"), Not(Var("B")))
//
//  // Define an environment where 'A' is true and 'B' is false
//  val env = Map("A" -> true, "B" -> false)
//
//  // Evaluate the example proposition
//  val result = eval(exampleProp, env)
//
//  // Print the result
//  println(s"The result of the propositional formula is $result")
}