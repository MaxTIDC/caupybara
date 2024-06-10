package Lib

/**
 * Logical values in three-valued-semantics: T, F, U (unknown).
 * Equivalent to balanced ternary numbers.
 */
enum Trilean:
  case T, F, U

/**
 * LTL evaluation for a given state in trace, using three-valued semantics.
 * Equivalent to Kleene / Priest logic semantics.
 */
object Trilean {
  def evalTrilean(pi: Execution, i: State, k: State, psi: LTL): Trilean = if i > k then Trilean.U else psi match {
    // Propositional
    case True => Trilean.T
    case False => Trilean.F

    case Not(phi) => evalTrilean(pi, i, k, phi) match {
      case Trilean.T => Trilean.F
      case Trilean.F => Trilean.T
      case Trilean.U => Trilean.U
    }

    case And(phi1, phi2) => (evalTrilean(pi, i, k, phi1), evalTrilean(pi, i, k, phi2)) match {
      case (Trilean.F, _) | (_, Trilean.F) => Trilean.F
      case (Trilean.T, x) => x
      case (x, Trilean.T) => x
      case (Trilean.U, Trilean.U) => Trilean.U
    }

    case Or(phi1, phi2) => (evalTrilean(pi, i, k, phi1), evalTrilean(pi, i, k, phi2)) match {
      case (Trilean.T, _) | (_, Trilean.T) => Trilean.T
      case (Trilean.F, x) => x
      case (x, Trilean.F) => x
      case (Trilean.U, Trilean.U) => Trilean.U
    }

    // Temporal
    case Atom(name) => if pi(i) contains name then Trilean.T else Trilean.F

    case X(phi) => evalTrilean(pi, i + 1, k, phi)
    case Y(phi) => if i > 0 then evalTrilean(pi, i - 1, k, phi) else Trilean.F

    case Lib.U(phi1, phi2) => evalTrilean(pi, i, k, Or(phi2, And(phi1, X(psi))))
    case G(phi) => evalTrilean(pi, i, k, And(phi, X(psi)))

    case _ => Trilean.U // Catch default
  }
}
