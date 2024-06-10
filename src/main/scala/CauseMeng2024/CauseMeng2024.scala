/** Newly proposed causality definition */

package CauseMeng2024

import Lib.*
import Lib.EvalFionda2016.eval

type CausalSet = Set[(State, String, Boolean)]

object Cause {
  /**
   * Compute all causes of violation to given LTL property in the counterexample execution,
   * from state i to k. Follows Meng 2024 definition of causality.
   *
   * Pre:
   * - NNF LTL formula must be in NNF.
   */
  def findViolationCauses(sigma: Execution, i: State, k: State, phi: LTL, bound: Int = 5): Set[CausalSet] = {
    var causes: Set[CausalSet] = Set()
    if eval(sigma, i, k, phi) then return causes // If trace does not violate property, return empty set

    var availableSingletons: CausalSet = getNegativeLiteralsAtoms(sigma, i, k, getLiterals(phi))

    for size <- 1 to math.min(availableSingletons.size, bound) do
      // Enumerate all subsets of size <- (1 to maximum)
      val candidateCausalSets: Iterator[CausalSet] = availableSingletons.subsets(size)

      // Find critical sets
      for cs <- candidateCausalSets do
        val sigmaCounter = flipAtomsInTrace(sigma, cs)
        if eval(sigmaCounter, i, k, phi) then
          causes += cs
          availableSingletons = availableSingletons diff cs

    causes
  }

  // Helper functions
  /**
   * Return a counterfactual trace where atoms in the CausalSet have their values flipped.
   */
  def flipAtomsInTrace(sigma: Execution, causeSet: CausalSet): Execution = {
    var newTrace = sigma

    for (state, name, _) <- causeSet do
      if sigma(state) contains name then
        newTrace += (state -> (newTrace(state) - name))
      else
        newTrace += (state -> (newTrace(state) + name))

    newTrace
  }

  /**
   * Return all (state, atom, value) where the atom belong to a literal in an NNF LTL formula,
   * and the literal is false on the trace at the state.
   *
   * Pre:
   * - literals extracted from NNF LTL formula.
   */
  def getNegativeLiteralsAtoms(sigma: Execution, i: State, k: State, literals: Set[LTL]): CausalSet = {
    def valuate(pi: Execution, j: State, atom: String): Boolean = pi(j) contains atom

    var cs: CausalSet = Set()
    for j <- i to k do
      for l <- literals do l match
        // Select literals that are negative
        case Atom(name) => if !valuate(sigma, j, name) then cs += ((j, name, valuate(sigma, j, name)))
        case Not(Atom(name)) => if valuate(sigma, j, name) then cs += ((j, name, valuate(sigma, j, name)))
        case _ => ;

    cs
  }
}
