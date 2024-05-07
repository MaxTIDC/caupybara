package Util

import Lib.*

import scala.util.parsing.combinator.*

/**
 * Parse input string into internal LTL representation.
 */
object LTLParser extends RegexParsers {
  // Factors (terminals)
  def truth: Parser[LTL] = "true" ^^ { _ => True }
  def falsity: Parser[LTL] = "false" ^^ { _ => False }
  def propAtom: Parser[Atom] = """[a-zA-Z_][a-zA-Z0-9_]*""".r ^^ { name => Atom(name) }

  // Term rules
  private def nextTerm: Parser[X] = "X" ~> factor ^^ { phi => X(phi) }
  private def eventuallyTerm: Parser[F] = "F" ~> factor ^^ { phi => F(phi) }
  private def alwaysTerm: Parser[G] = "G" ~> factor ^^ { phi => G(phi) }
  private def notTerm: Parser[Not] = "!" ~> factor ^^ { phi => Not(phi) }

  // Special shorthands for GR(1)
  private def GFExpr: Parser[LTL] = "GF" ~> term ^^ { phi => G(F(phi)) }
  private def FGExpr: Parser[LTL] = "FG" ~> term ^^ { phi => F(G(phi)) }

  // Expression rules
  private def andExpr: Parser[And] = term ~ ("&" ~> term) ^^ { case phiL ~ phiR => And(phiL, phiR) }
  private def orExpr: Parser[Or] = term ~ ("|" ~> term) ^^ { case phiL ~ phiR => Or(phiL, phiR) }
  private def impliesExpr: Parser[Implies] = term ~ ("->" ~> term) ^^ { case phiL ~ phiR => Implies(phiL, phiR) }
  private def iffExpr: Parser[Iff] = term ~ ("<->" ~> term) ^^ { case phiL ~ phiR => Iff(phiL, phiR) }
  private def untilExpr: Parser[U] = term ~ ("U" ~> term) ^^ { case phiL ~ phiR => U(phiL, phiR) }

  // LTL grammar
  private def expr: Parser[LTL] = andExpr | orExpr | impliesExpr | iffExpr | untilExpr | GFExpr | FGExpr | term
  private def term: Parser[LTL] = notTerm | nextTerm | eventuallyTerm | alwaysTerm | factor
  private def factor: Parser[LTL] = truth | falsity | propAtom | ("(" ~> expr <~ ")") | expr

  private def parseLTL(str: String): ParseResult[LTL] = parseAll(expr, str)
  def apply(str: String): LTL = LTLParser.parseLTL(str) match {
    case LTLParser.Success(result: LTL, _) => result
    case _ => sys.error("Could not parse the input string: " + str)
  }
}
