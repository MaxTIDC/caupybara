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
//type AtomicProps = Set[String]
type State = Int
type Trace = Map[State, Set[String]]
