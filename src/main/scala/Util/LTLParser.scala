package Util

import Lib.*
import scala.util.parsing.combinator._

object LTLParser extends RegexParsers {
  def term: Parser[LTL] = atom | notExpr | andExpr | orExpr | impliesExpr |
    nextExpr | eventuallyExpr | alwaysExpr | untilExpr | ("(" ~> expr <~ ")")
  def expr: Parser[LTL] = term

//  def reservedWords: Parser[String] = guard("X" | "F" | "G" | "U")
//  def notReservedIdentifier: Parser[String] = not(reservedWords) ~> ident

  // TODO: Find out how True and False represented in Spectra
  def notExpr: Parser[Not] = "!" ~> term ^^ { phi => Not(phi) }
  def andExpr: Parser[And] = term ~ ("&" ~> term) ^^ { case phiL ~ phiR => And(phiL, phiR) }
  def orExpr: Parser[Or] = term ~ ("|" ~> term) ^^ { case phiL ~ phiR => Or(phiL, phiR) }
  def impliesExpr: Parser[Implies] = term ~ ("->" ~> term) ^^ { case phiL ~ phiR => Implies(phiL, phiR) }

  def nextExpr: Parser[X] = "X" ~> term ^^ { phi => X(phi) }
  def eventuallyExpr: Parser[F] = "F" ~> term ^^ { phi => F(phi) }
  def alwaysExpr: Parser[G] = "G" ~> term ^^ { phi => G(phi) }
  def untilExpr: Parser[U] = term ~ ("U" ~> term) ^^ { case phiL ~ phiR => U(phiL, phiR) }

  def atom: Parser[Atom] = """[a-zA-Z0-9]+""".r ^^ { name => Atom(name) }

  def parseLTL(str: String): ParseResult[LTL] = parseAll(expr, str)
  def apply(str: String): LTL = LTLParser.parseLTL(str) match {
    case LTLParser.Success(result: LTL, _) => result
    case _ => sys.error("Could not parse the input string: " + str)
  }
}
