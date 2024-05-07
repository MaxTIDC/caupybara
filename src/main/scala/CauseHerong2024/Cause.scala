/** Newly proposed causality definition */

package CauseHerong2024

import Lib.*
import scala.collection.mutable

type CausalSet = Set[(State, String, Boolean)]

object Cause {
  var causes: Set[CausalSet] = Set()
  var propAtoms: PropAtoms = Set()

  def causeTrilean(pi: Trace, i: State, k: State, psi: LTL): Set[CausalSet] = {
    if i >= k then
      Set()
    else
      Set()  // TODO
  }

  // Helper functions
  /**
   * Find all minimal critical sets of a certain size,
   * using ONLY the given propositional atoms.
   */
  def findMinCritSetsOfSize(pi: Trace, i: State, k: State, psi: LTL,
                            propAtoms: PropAtoms, size: Int): Set[CausalSet] = {
    val candidates: Set[CausalSet] = subsetsOfSize(getMaximalCausalSet(pi, i, k, propAtoms), size)
    var causalSets: Set[CausalSet] = Set()

    for cs <- candidates do
      val newPi = flipAtomsInTrace(pi, cs)
      if evalTrilean(newPi, i, k, psi) != Trilean.F then  // Using 3VL evaluation here
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
   * Return the maximal possible CausalSet on the trace between state i-k,
   * using ONLY the given propositional atoms
   */
  def getMaximalCausalSet(pi: Trace, i: State, k: State, propAtoms: PropAtoms): CausalSet = {
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
