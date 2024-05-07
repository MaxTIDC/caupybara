package Lib

/** Abstract data type for LTL formulae */
sealed trait LTL

case object True extends LTL
case object False extends LTL
case class Atom(name: String) extends LTL

case class Not(right: LTL) extends LTL
case class And(left: LTL, right: LTL) extends LTL
case class Or(left: LTL, right: LTL) extends LTL
case class Implies(left: LTL, right: LTL) extends LTL
case class Iff(left: LTL, right: LTL) extends LTL

case class X(right: LTL) extends LTL
case class F(right: LTL) extends LTL
case class G(right: LTL) extends LTL
case class U(left: LTL, right: LTL) extends LTL

// Type aliases
type PropAtoms = Set[String]
type State = Int
type Trace = Map[State, Set[String]]

// Methods
/** */
def getAtoms(psi: LTL): PropAtoms = psi match
  case Atom(name) => Set(name)

  case Not(phi) => getAtoms(phi)
  case X(phi) => getAtoms(phi)
  case F(phi) => getAtoms(phi)
  case G(phi) => getAtoms(phi)

  case And(phiL, phiR) => getAtoms(phiL) union getAtoms(phiR)
  case Or(phiL, phiR) => getAtoms(phiL) union getAtoms(phiR)
  case Implies(phiL, phiR) => getAtoms(phiL) union getAtoms(phiR)
  case Iff(phiL, phiR) => getAtoms(phiL) union getAtoms(phiR)
  case U(phiL, phiR) => getAtoms(phiL) union getAtoms(phiR)
  case _ => Set()
