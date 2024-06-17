package Util

import Lib.*

/**
 * Converts LTL to negation normal form, i.e. contains only:
 *  - G, U, X, And, Or
 *  - literals (atoms + negated atoms)
 */
def toNNF(phi: LTL): LTL = toNNFHelper(toImplFree(phi))

def toNNFHelper(phi: LTL): LTL = phi match
  // Propositional
  case Not(Not(a)) => toNNFHelper(a)

  case And(a, b) => And(toNNFHelper(a), toNNFHelper(b))
  case Not(And(a, b)) => toNNFHelper(Or(Not(a), Not(b)))

  case Or(a, b) => Or(toNNFHelper(a), toNNFHelper(b))
  case Not(Or(a, b)) => toNNFHelper(And(Not(a), Not(b)))

  // Temporal
  case U(phi1, phi2) => U(toNNFHelper(phi1), toNNFHelper(phi2))
  case Not(U(phi1, phi2)) => toNNFHelper(Or(U(Not(phi2), And(Not(phi1), Not(phi2))), G(Not(phi2))))

  case G(phi) => G(toNNFHelper(phi))
  case Not(G(phi)) => toNNFHelper(F(Not(phi)))

  case F(phi) => U(True, toNNFHelper(phi))
  case Not(F(phi)) => toNNFHelper(G(Not(phi)))

  case X(phi) => X(toNNFHelper(phi))
  case Not(X(phi)) => toNNFHelper(X(Not(phi)))

  case Y(phi) => Y(toNNFHelper(phi))
  case Not(Y(phi)) => toNNFHelper(Y(Not(phi)))

  // Default catch
  case a => a

/**
 * Removes Implies' and Iffs' from LTL formula.
 */
def toImplFree(phi: LTL): LTL = phi match
  // Actual conversions
  case Implies(a, b) => Or(Not(toImplFree(a)), toImplFree(b))
  case Iff(a, b) => And(toImplFree(Implies(a, b)), toImplFree(Implies(b, a)))

  // Preserved
  case Not(a) => Not(toImplFree(a))
  case And(a, b) => And(toImplFree(a), toImplFree(b))
  case Or(a, b) => Or(toImplFree(a), toImplFree(b))

  case X(a) => X(toImplFree(a))
  case F(a) => F(toImplFree(a))
  case G(a) => G(toImplFree(a))
  case U(a, b) => U(toImplFree(a), toImplFree(b))

  case Y(a) => Y(toImplFree(a))

  // Default catch for atoms, true, false
  case a => a
