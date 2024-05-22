/** Newly proposed causality definition */

package CauseMeng2024

import Lib.*

import scala.collection.mutable

type CausalSet = Set[(State, String, Boolean)]

object Cause {
  /**
   * Compute all causes of violation to given LTL property in the trace,
   * from state i to k. Follows Meng 2024 definition of causality.
   *
   * Pre:
   * - NNF LTL formula must be in NNF.
   */
  def findViolationCauses(pi: Trace, i: State, k: State, psi: LTL, maxSize: Int = 5): Set[CausalSet] = {
    var causes: Set[CausalSet] = Set()
    var availableAtomVals: CausalSet = getNegativeLiteralsAtoms(pi, i, k, getLiterals(psi))

    // If property not violated by trace, return empty set
    if evalTrilean(pi, i, k, psi) != Trilean.F then return causes // Using 3VL evaluation here

    for size <- 1 to math.min(availableAtomVals.size, maxSize + 1) do
      // Enumerate all subsets of size <- (1 to maximum)
      val candidateCausalSets: Iterator[CausalSet] = availableAtomVals.subsets(size)

      // Find critical sets
      for cs <- candidateCausalSets do
        val newTrace = flipAtomsInTrace(pi, cs)
        if evalTrilean(newTrace, i, k, psi) != Trilean.F then // Using 3VL evaluation here
          causes += cs
          availableAtomVals = availableAtomVals diff cs

    causes
  }

  // Helper functions
  /**
   * Return a counterfactual trace where atoms in the CausalSet have their values flipped.
   */
  def flipAtomsInTrace(pi: Trace, causeSet: CausalSet): Trace = {
    val newTrace = mutable.Map[Int, Set[String]](pi.toSeq: _*)

    for (state, name, _) <- causeSet do {
      if pi(state) contains name then
        newTrace.update(state, newTrace(state) - name)
      else
        newTrace.update(state, newTrace(state) + name)
    }

    newTrace.toMap
  }

  /**
   * Return all (state, atom, value) where the atom belong to a literal in an NNF LTL formula,
   * and the literal is false on the trace at the state.
   *
   * Pre:
   * - literals extracted from NNF LTL formula.
   */
  def getNegativeLiteralsAtoms(pi: Trace, i: State, k: State, literals: Set[LTL]): CausalSet = {
    def valuate(pi: Trace, j: State, atom: String): Boolean = pi(j) contains atom

    var cs: CausalSet = Set()
    for j <- i to k do
      for l <- literals do l match
        // Select literals that are negative
        case Atom(name) => if !valuate(pi, j, name) then cs += ((j, name, valuate(pi, j, name)))
        case Not(Atom(name)) => if valuate(pi, j, name) then cs += ((j, name, valuate(pi, j, name)))
        case _ => ;

    cs
  }
}
