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
  var m: State = -1
  var n: State = -1
  var sat: Set[(LTL, State)] = Set()

  // Public interface
  def eval(pi: Execution, m: State, n: State, phi: LTL): Boolean = {
    this.m = m
    this.n = n
    this.sat = Set()
    evalFionda2016(pi, phi)
    sat contains(phi, m)
  }

  private def evalFionda2016(pi: Execution, phi: LTL): Unit = {
    if !(sat contains(phi, m)) then phi match {
      // Original
      case Atom(name) =>
        for i <- m to n do
          if pi(i) contains name then sat += (phi, i)
      case Not(Atom(name)) =>
        for i <- m to n do
          if !(pi(i) contains name) then sat += (phi, i)

      case And(psi1, psi2) =>
        evalFionda2016(pi, psi1)
        evalFionda2016(pi, psi2)
        for i <- m to n do
          if (sat contains(psi1, i)) & (sat contains(psi2, i)) then sat += (phi, i)
      case Or(psi1, psi2) =>
        evalFionda2016(pi, psi1)
        evalFionda2016(pi, psi2)
        for i <- m to n do
          if (sat contains(psi1, i)) | (sat contains(psi2, i)) then sat += (phi, i)

      case X(psi) => // Weak next
        evalFionda2016(pi, psi)
        for i <- m to n do
          if (i + 1 > n) | (sat contains(psi, i + 1)) then sat += (phi, i)

      case F(psi) =>
        evalFionda2016(pi, psi)
        var j = m
        for i <- m to n do
          if sat contains(psi, i) then
            while j <= i do
              sat += (phi, j)
              j += 1
      //        for j <- m to n do
      //          boundary:
      //            for i <- j to n do
      //              if sat contains(psi, i) then
      //                sat += (phi, j)
      //                break()

      case G(psi) =>
        evalFionda2016(pi, psi)
        var j = m
        while j <= n do
          boundary:
            for i <- j to n do
              if !(sat contains(psi, i)) then
                j = i + 1
                break()
            sat += (phi, j)
          j += 1
      //        for i <- m to n do
      //          boundary:
      //            for i <- j to n do
      //              if !(sat contains(psi, i)) then break()
      //            sat += (phi, j)

      // Additional rules
      case True => for i <- m to n do sat += (phi, i)

      case Y(psi) =>
        evalFionda2016(pi, psi)
        for i <- m to (n - 1) do // PastLTL operator for Spectra
          if sat contains(psi, i) then sat += (phi, i + 1)

      case U(psi1, psi2) =>
        evalFionda2016(pi, psi1)
        evalFionda2016(pi, psi2)
        for j <- m to n do
          boundary:
            for i <- j to n do
              if sat contains(psi2, i) then sat += (phi, j)
              if !(sat contains(psi1, i)) then break()

      case _ => ;
    }
  }
}
