package Lib

import scala.util.boundary
import scala.util.boundary.break

/**
 * Finite LTL evaluation for a given state in trace,
 * By Fionda et al. 2016.
 *
 * Pre: LTL formula is in NNF.
 */
object EvalFionda2016 {
  private var m: State = -1
  private var n: State = -1
  private var sat: Map[LTL, Set[State]] = Map()

  // Public interface
  def eval(pi: Execution, m: State, n: State, phi: LTL): Boolean = {
    this.m = m
    this.n = n
    this.sat = Map()
    evalFionda2016(pi, phi)

    sat(phi) contains m
  }

  private def evalFionda2016(sigma: Execution, phi: LTL): Unit = {
    if sat contains phi then return

    var satPhi: Set[State] = Set()

    phi match {
      // Original
      case Atom(name) =>
        satPhi = (m to n).filter(i => sigma(i) contains name).toSet

      case Not(Atom(name)) =>
        satPhi = (m to n).filter(i => !(sigma(i) contains name)).toSet

      case And(psi1, psi2) =>
        evalFionda2016(sigma, psi1)
        evalFionda2016(sigma, psi2)
        val satPsi1 = sat(psi1)
        val satPsi2 = sat(psi2)
        satPhi = (m to n).filter(i => (satPsi1 contains i) & (satPsi2 contains i)).toSet

      case Or(psi1, psi2) =>
        evalFionda2016(sigma, psi1)
        evalFionda2016(sigma, psi2)
        val satPsi1 = sat(psi1)
        val satPsi2 = sat(psi2)
        satPhi = (m to n).filter(i => (satPsi1 contains i) | (satPsi2 contains i)).toSet

      case X(psi) => // Weak next
        evalFionda2016(sigma, psi)
        val satPsi = sat(psi)
        satPhi = (m to n).filter(i => (satPsi contains i + 1) | (i + 1 > n)).toSet

      case F(psi) =>
        evalFionda2016(sigma, psi)
        val satPsi = sat(psi)
        for j <- m to n do
          boundary:
            for i <- j to n do
              if satPsi contains i then
                satPhi += j
                break()

      case G(psi) =>
        evalFionda2016(sigma, psi)
        val satPsi = sat(psi)
        for j <- m to n do
          boundary:
            for i <- j to n do
              if !(satPsi contains i) then break()
            satPhi += j

      // Additional rules
      case True => satPhi = (m to n).toSet

      case Y(psi) =>
        evalFionda2016(sigma, psi)
        val satPsi = sat(psi)
        satPhi = (m + 1 to n).filter(i => satPsi contains i - 1).toSet

      case U(psi1, psi2) =>
        evalFionda2016(sigma, psi1)
        evalFionda2016(sigma, psi2)
        val satPsi1 = sat(psi1)
        val satPsi2 = sat(psi2)
        for j <- m to n do
          boundary:
            for i <- j to n do
              if satPsi2 contains i then satPhi += j
              if !(satPsi1 contains i) then break()

      case _ => ;
    }

    sat += (phi -> satPhi)
  }
}
