/** Newly proposed causality definition */

package CauseHerong2024

import Lib.*
import Util.toNNF

import scala.collection.mutable

type CausalSet = Set[(State, String, Boolean)]

object Cause {
  private var causes: Set[CausalSet] = Set()
  private var usedAtomVals: CausalSet = Set()

  /**
   * Compute all causes of violation to given LTL property in the trace,
   * from state i to k. Follows Herong 2024 definition of causality.
   */
  def findViolationCauses(pi: Trace, i: State, k: State, psi: LTL): Set[CausalSet] = {
    var availableAtomVals: CausalSet = getNegativeLiteralsAtoms(pi, i, k, getLiterals(toNNF(psi))) diff usedAtomVals

    var size: Int = 1
    while size <= availableAtomVals.size do
      val candidateCausalSets: Set[CausalSet] = subsetsOfSize(availableAtomVals, size)

      val newCauses = findCritSets(pi, i, k, psi, candidateCausalSets)
      causes = causes union newCauses
      for cs <- newCauses do
        usedAtomVals = usedAtomVals union cs

      size += 1
      availableAtomVals = availableAtomVals diff usedAtomVals

    causes
  }

  def reset(): Unit = {
    causes = Set()
    usedAtomVals = Set()
  }

  // Helper functions
  /**
   * Find all critical sets from given set of candidates.
   */
  def findCritSets(pi: Trace, i: State, k: State, psi: LTL, candidates: Set[CausalSet]): Set[CausalSet] = {
    var causalSets: Set[CausalSet] = Set()

    for cs <- candidates do
      val newPi = flipAtomsInTrace(pi, cs)
      if evalTrilean(newPi, i, k, psi) != Trilean.F then // Using 3VL evaluation here
        causalSets += cs

    causalSets
  }

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
