package Lib

/**
 * Finite LTL evaluation for a given state in trace,
 * By Fionda et al. 2016.
 *
 * Pre: LTL formula is in PNF.
 */
object EvalFionda2016 {
  // Public interface
  def eval(pi: Execution, m: State, n: State, phi: LTL): Boolean = {
    evalFionda2016(pi, m, n, phi) contains m
  }

  private def evalFionda2016(pi: Execution, m: State, n: State, phi: LTL): Set[State] = {
    var sat: Set[State] = Set()
    phi match {
      // Original
      case True => for i <- m to n do sat += i
      case False => ;

      case Atom(name) => for i <- m to n do
        if pi(i) contains name then sat += i
      case Not(Atom(name)) => for i <- m to n do
        if !(pi(i) contains name) then sat += i

      case And(psi1, psi2) =>
        sat = evalFionda2016(pi, m, n, psi1) intersect evalFionda2016(pi, m, n, psi2)
      case Or(psi1, psi2) =>
        sat = evalFionda2016(pi, m, n, psi1) union evalFionda2016(pi, m, n, psi2)

      case X(psi) => for i <- m to n do
        if i > 0 & (evalFionda2016(pi, m, n, psi) contains i) then sat += (i - 1)

      case F(psi) =>
        for j <- m to n do
          for i <- j to n do
            if evalFionda2016(pi, m, n, psi) contains i then sat += j

      case G(psi) =>
        for j <- m to n do
          var flag: Boolean = true
          for i <- j to n do
            if !(evalFionda2016(pi, m, n, psi) contains i) then flag = false
          if flag then sat += j

      // Additional rules
      case Y(psi) => for i <- m to (n - 1) do // PastLTL operator for Spectra
        if evalFionda2016(pi, m, n, psi) contains i then sat += (i + 1)

      case U(psi1, psi2) =>
        for j <- m to n do
          var flag: Boolean = true
          for i <- j to n do
            if flag & (evalFionda2016(pi, m, n, psi2) contains i) then sat += j
            if !(evalFionda2016(pi, m, n, psi1) contains i) then flag = false

      case _ => ;
    }

    sat
  }
}
