package Util

import Lib.*

import scala.util.parsing.combinator.*

object LTLParser extends RegexParsers {
  // Factors (terminals)
  def truth: Parser[LTL] = "true" ^^ { _ => True }
  def falsity: Parser[LTL] = "false" ^^ { _ => False }
  def propAtom: Parser[Atom] = """[a-zA-Z_][a-zA-Z0-9_]*""".r ^^ { name => Atom(name) }

  // Term rules
  def nextTerm: Parser[X] = "X" ~> factor ^^ { phi => X(phi) }
  def eventuallyTerm: Parser[F] = "F" ~> factor ^^ { phi => F(phi) }
  def alwaysTerm: Parser[G] = "G" ~> factor ^^ { phi => G(phi) }
  def notTerm: Parser[Not] = "!" ~> factor ^^ { phi => Not(phi) }

  def GFExpr: Parser[LTL] = "GF" ~> term ^^ { phi => G(F(phi)) }  // Special shorthand for GR(1)
  def FGExpr: Parser[LTL] = "FG" ~> term ^^ { phi => F(G(phi)) }  // Special shorthand for GR(1)

  // Expression rules
  def andExpr: Parser[And] = term ~ ("&" ~> term) ^^ { case phiL ~ phiR => And(phiL, phiR) }
  def orExpr: Parser[Or] = term ~ ("|" ~> term) ^^ { case phiL ~ phiR => Or(phiL, phiR) }
  def impliesExpr: Parser[Implies] = term ~ ("->" ~> term) ^^ { case phiL ~ phiR => Implies(phiL, phiR) }
  def iffExpr: Parser[Iff] = term ~ ("<->" ~> term) ^^ { case phiL ~ phiR => Iff(phiL, phiR) }
  def untilExpr: Parser[U] = term ~ ("U" ~> term) ^^ { case phiL ~ phiR => U(phiL, phiR) }

  // LTL grammar
  def expr: Parser[LTL] = andExpr | orExpr | impliesExpr | iffExpr | untilExpr | GFExpr | FGExpr | term
  def term: Parser[LTL] = notTerm | nextTerm | eventuallyTerm | alwaysTerm | factor
  def factor: Parser[LTL] = truth | falsity | propAtom | ("(" ~> expr <~ ")") | expr

  def parseLTL(str: String): ParseResult[LTL] = parseAll(expr, str)
  def apply(str: String): LTL = LTLParser.parseLTL(str) match {
    case LTLParser.Success(result: LTL, _) => result
    case _ => sys.error("Could not parse the input string: " + str)
  }
}
