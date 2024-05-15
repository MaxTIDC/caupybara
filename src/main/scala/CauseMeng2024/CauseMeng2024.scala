/** Newly proposed causality definition */

package CauseMeng2024

import Lib.*
import Util.toNNF

import scala.collection.mutable

type CausalSet = Set[(State, String, Boolean)]

object Cause {
//  private var causes: Set[CausalSet] = Set()
//  private var usedAtomVals: CausalSet = Set()

  /**
   * Compute all causes of violation to given LTL property in the trace,
   * from state i to k. Follows Meng 2024 definition of causality.
   */
  def findViolationCauses(pi: Trace, i: State, k: State, psi: LTL, maxSize: Int = 3): Set[CausalSet] = {
    var causes: Set[CausalSet] = Set()
    var availableAtomVals: CausalSet = getNegativeLiteralsAtoms(pi, i, k, getLiterals(toNNF(psi))) // diff usedAtomVals

    if evalTrilean(pi, i, k, psi) != Trilean.F then return causes

    for size <- 1 to math.min(availableAtomVals.size, maxSize + 1) do
      val candidateCausalSets: Set[CausalSet] = subsetsOfSize(availableAtomVals, size)

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
   * Return a counterfactual trace where
   * atoms in the CausalSet have their values flipped.
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
    def atomTrueAt(pi: Trace, j: State, atom: String): Boolean = pi(j) contains atom

    var cs: CausalSet = Set()
    for j <- i to k do
      for l <- literals do l match
        // Select literals that are negative
        case Atom(name) => if !atomTrueAt(pi, j, name) then cs += ((j, name, atomTrueAt(pi, j, name)))
        case Not(Atom(name)) => if atomTrueAt(pi, j, name) then cs += ((j, name, atomTrueAt(pi, j, name)))
        case _ => ;

    cs
  }
}

/**
 * Enumerate all subsets of a set of a certain size.
 */
def subsetsOfSize[T](set: Set[T], size: Int): Set[Set[T]] = size match
  case 0 => Set(Set.empty)
  case s if s == set.size => Set(set)
  case s if s > set.size | s < 0 => Set.empty
  case _ => set.toList.combinations(size).map(_.toSet).toSet
