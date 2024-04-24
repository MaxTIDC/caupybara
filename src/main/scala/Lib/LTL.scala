package Lib

/* ADT for LTL */
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

//// LTL evaluation in a TS
//def eval(phi: LTL, env: Map[String, Boolean]): Boolean = phi match {
//  case True => true
//  case False => false
//  case Atom(name) => env.getOrElse(name, false)
//  case Not(p) => !eval(p, env)
//  case And(p1, p2) => eval(p1, env) && eval(p2, env)
//  case Or(p1, p2) => eval(p1, env) || eval(p2, env)
//  case _ => false
//}

/* Type aliases */
type State = Int

type AtomicProps = Set[String]
type Trace = Map[State, Set[String]]
