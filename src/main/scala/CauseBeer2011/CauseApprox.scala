/** Methods here are the implementation of cause computation heuristic by Beer et al. 2011 */

package CauseBeer2011

import Lib.*

type CausalPair = (State, String)
val emptySet = Set()

/**
 * The "C(pi^i^, psi)" heuristic (Beer et al. 2011).
 * To compute cause on entire path, use i = 0.
 *
 * Pre:
 * - NNF LTL formula must be in NNF.
 */
def causeApprox(pi: Trace, i: State, psi: LTL): Set[CausalPair] = psi match
  case True => Set()
  case False => Set()
  case Atom(p) =>
    if !(pi(i) contains p) then Set((i, p)) else Set()
  case Not(Atom(p)) =>
    if pi(i) contains p then Set((i, p)) else Set()
  case X(phi) =>
    if i < pi.size-1 then causeApprox(pi, i+1, phi) else Set()
  case And(phiL, phiR) =>
    causeApprox(pi, i, phiL) union causeApprox(pi, i, phiR)
  case Or(phiL, phiR) =>
    if valFunc(pi, i, phiL) == 0 && valFunc(pi, i, phiR) == 0 then
      causeApprox(pi, i, phiL) union causeApprox(pi, i, phiR)
    else
      Set()
  case G(phi) =>
    if valFunc(pi, i, phi) == 0 then
      causeApprox(pi, i, phi)
    else if valFunc(pi, i, phi) == 1 && i < pi.size-1 && valFunc(pi, i, X(G(phi))) == 0 then
      causeApprox(pi, i+1, G(phi))
    else
      Set()
  case U(phiL, phiR) =>
    if valFunc(pi, i, phiL) == 0 && valFunc(pi, i, phiR) == 0 then
      causeApprox(pi, i, phiR) union causeApprox(pi, i, phiL)
    else if valFunc(pi, i, phiL) == 1 && valFunc(pi, i, phiR) == 0 && i == pi.size-1 then
      causeApprox(pi, i, phiR)
    else if valFunc(pi, i, phiL) == 1 && valFunc(pi, i, phiR) == 0
        && i < pi.size-1 && valFunc(pi, i, X(U(phiL, phiR))) == 0 then
      causeApprox(pi, i, phiR) union causeApprox(pi, i+1, U(phiL, phiR))
    else
      Set()
  case _ =>   // Do not catch equivalences
    throw new RuntimeException("LTL formula needs to be in NNF")

/** The "val(pi^i^, psi)" helper function */
def valFunc(pi: Trace, i: State, phi: LTL): Int = phi match
  case True => 1
  case False => 0
  case _ => if causeApprox(pi, i, phi) == emptySet then 1 else 0