package Util

import Lib.*

import scala.util.parsing.combinator.*

/**
 * Parse input string into internal LTL representation.
 */
object LTLParser extends RegexParsers {
  // Special Spectra expressions
  private def spAlwEvExpr: Parser[LTL] = "alwEv" ~> expr ^^ { phi => G(F(phi)) }

  private def spAlwExpr: Parser[LTL] = "alw" ~> expr ^^ { phi => G(phi) }

  private def spIniExpr: Parser[LTL] = "ini" ~> expr ^^ identity

  private def previous: Parser[Y] = ("Y" | "PREV") ~> factor ^^ { phi => Y(phi) }

  // Factors (terminals)
  private def truth: Parser[LTL] = "true" ^^ { _ => True }
  private def falsity: Parser[LTL] = "false" ^^ { _ => False }
  private def propAtom: Parser[Atom] = """[a-zA-Z_][a-zA-Z0-9_]*""".r ^^ { name => Atom(name) }

  // Unary operator rules
  private def next: Parser[X] = ("X" | "next") ~> factor ^^ { phi => X(phi) }
  private def eventually: Parser[F] = "F" ~> factor ^^ { phi => F(phi) }
  private def always: Parser[G] = "G" ~> factor ^^ { phi => G(phi) }
  private def not: Parser[Not] = "!" ~> factor ^^ { phi => Not(phi) }

  // Special expressions for GR(1)
  private def GFExpr: Parser[LTL] = "GF" ~> expr ^^ { phi => G(F(phi)) }
  private def FGExpr: Parser[LTL] = "FG" ~> expr ^^ { phi => F(G(phi)) }

  // LTL grammar
  private def expr: Parser[LTL] = spAlwEvExpr | spAlwExpr | spIniExpr | GFExpr | FGExpr | binOps1

  private def binOps1: Parser[LTL] = binOps2 ~ rep(("<->" | "->") ~ binOps2) ^^ {
    case sub ~ list => list.foldLeft(sub) {
      case (phiL, "<->" ~ phiR) => Iff(phiL, phiR)
      case (phiL, "->" ~ phiR) => Implies(phiL, phiR)
    }
  }

  private def binOps2: Parser[LTL] = binOps3 ~ rep(("|" | "||") ~ binOps3) ^^ {
    case sub ~ list => list.foldLeft(sub) {
      case (phiL, "|" ~ phiR) => Or(phiL, phiR)
      case (phiL, "||" ~ phiR) => Or(phiL, phiR)
    }
  }

  private def binOps3: Parser[LTL] = binOps4 ~ rep(("&" | "&&") ~ binOps4) ^^ {
    case sub ~ list => list.foldLeft(sub) {
      case (phiL, "&" ~ phiR) => And(phiL, phiR)
      case (phiL, "&&" ~ phiR) => And(phiL, phiR)
    }
  }

  private def binOps4: Parser[LTL] = unOps ~ rep("U" ~ unOps) ^^ {
    case sub ~ list => list.foldLeft(sub) {
      case (phiL, "U" ~ phiR) => U(phiL, phiR)
    }
  }

  private def unOps: Parser[LTL] = eventually | always | next | previous | not | factor

  private def factor: Parser[LTL] = truth | falsity | propAtom | ("(" ~> expr <~ ")")

  // Parser methods
  private def parseLTL(str: String): ParseResult[LTL] = parseAll(expr, str)
  def apply(str: String): LTL = LTLParser.parseLTL(str) match {
    case Success(result: LTL, _) => result
    case failure: NoSuccess => sys.error("Could not parse the input string: " + str + "\n" + failure)
  }
}
