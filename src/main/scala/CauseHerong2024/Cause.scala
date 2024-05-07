/** Newly proposed causality definition */

package CauseHerong2024

import Lib.*
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
    var availableAtomVals: CausalSet = getFullAtomVals(pi, i, k, getAtoms(psi)) diff usedAtomVals

    var size: Int = 1
    while size <= availableAtomVals.size do
      val candidates: Set[CausalSet] = subsetsOfSize(availableAtomVals, size)

      val newCauses = findCritSets(pi, i, k, psi, candidates)
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
   * Return all atomic valuations on the trace from state i to k,
   * given in CausalSet format using ONLY the given propositional atoms.
   */
  def getFullAtomVals(pi: Trace, i: State, k: State, propAtoms: PropAtoms): CausalSet = {
    var cs: CausalSet = Set()
    for j <- i to k do
      for atom <- propAtoms do
        cs += ((j, atom, pi(j) contains atom))

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
