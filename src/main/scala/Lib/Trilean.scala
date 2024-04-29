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
def eval(pi: Trace, i: State, psi: LTL): Trilean = psi match {
  // Propositional
  case True => Trilean.T
  case False => Trilean.F

  case Not(phi) => eval(pi, i, phi) match {
    case Trilean.T => Trilean.F
    case Trilean.F => Trilean.T
    case Trilean.U => Trilean.U
  }

  case And(phi1, phi2) => (eval(pi, i, phi1), eval(pi, i, phi2)) match {
    case (Trilean.F, _) | (_, Trilean.F) => Trilean.F
    case (Trilean.T, x) => x
    case (x, Trilean.T) => x
    case (Trilean.U, Trilean.U) => Trilean.U
  }

  case Or(phi1, phi2) => (eval(pi, i, phi1), eval(pi, i, phi2)) match {
    case (Trilean.T, _) | (_, Trilean.T) => Trilean.T
    case (Trilean.F, x) => x
    case (x, Trilean.F) => x
    case (Trilean.U, Trilean.U) => Trilean.U
  }

  // Temporal
//  case Atom(name) => {
//    if pi(i) contains name
//  }

  case _ => Trilean.U  // TODO
}
