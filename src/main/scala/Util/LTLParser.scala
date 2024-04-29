package Util

import Lib.*

import scala.util.parsing.combinator.*
import scala.util.parsing.input.*

sealed trait LTLToken

case object TrueToken extends LTLToken
case object FalseToken extends LTLToken
case class AtomToken(name: String) extends LTLToken

case object NotToken extends LTLToken
case object AndToken extends LTLToken
case object OrToken extends LTLToken
case object ImpliesToken extends LTLToken
case object IffToken extends LTLToken

case object XToken extends LTLToken
case object GToken extends LTLToken
case object FToken extends LTLToken
case object UToken extends LTLToken

case object LParen extends LTLToken
case object RParen extends LTLToken
//case object EqToken extends LTLTokens

trait LTLCompilationError
case class LTLLexerError(msg: String) extends LTLCompilationError

object LTLLexer extends RegexParsers {
  def t: Parser[LTLToken] = "true" ^^ (_ => TrueToken)
  def f: Parser[LTLToken] = "false" ^^ (_ => FalseToken)
  def atom: Parser[LTLToken] = """[a-zA-Z_][a-zA-Z0-9_]*""".r ^^ { name => AtomToken(name) }

  def not: Parser[LTLToken] = "!" ^^ (_ => NotToken)
  def and: Parser[LTLToken] = "&" ^^ (_ => AndToken)
  def or: Parser[LTLToken] = "|" ^^ (_ => OrToken)
  def implies: Parser[LTLToken] = "->" ^^ (_ => ImpliesToken)
  def iff: Parser[LTLToken] = "<->" ^^ (_ => IffToken)

  def X: Parser[LTLToken] = "X" ^^ (_ => XToken)
  def G: Parser[LTLToken] = "G" ^^ (_ => GToken)
  def F: Parser[LTLToken] = "F" ^^ (_ => FToken)
  def U: Parser[LTLToken] = "U" ^^ (_ => UToken)

  def lParen: Parser[LTLToken] = "(" ^^ (_ => LParen)
  def rParen: Parser[LTLToken] = ")" ^^ (_ => RParen)

  def tokens: Parser[List[LTLToken]] = {
    phrase(rep1(lParen | rParen | X | G | F | U | not
      | and | or | iff | implies | t | f | atom)) ^^ identity
  }

  def apply(input: String):Either[LTLLexerError, List[LTLToken]] = {
    parse(tokens, input) match {
      case NoSuccess(msg, next) => Left(LTLLexerError(msg))
      case Success(result, next) => Right(result)
    }
  }
}

object LTLParser extends Parsers {
  override type Elem = LTLToken

  class WorkflowTokenReader(tokensList: Seq[LTLToken]) extends Reader[LTLToken] {
    override def first: LTLToken = tokensList.head
    override def atEnd: Boolean = tokensList.isEmpty
    override def pos: Position = NoPosition
    override def rest: Reader[LTLToken] = new WorkflowTokenReader(tokensList.tail)
  }

//  def propAtom: Parser[LTL] = AtomToken ^^ { name => Atom(phi) }

  //  def term: Parser[LTL] = atom | notExpr | andExpr | orExpr | impliesExpr |
  //    nextExpr | eventuallyExpr | alwaysExpr | untilExpr | ("(" ~> expr <~ ")")
  //  def expr: Parser[LTL] = term

  //  def notExpr: Parser[Not] = "!" ~> term ^^ { phi => Not(phi) }
  //  def andExpr: Parser[And] = term ~ ("&" ~> term) ^^ { case phiL ~ phiR => And(phiL, phiR) }
  //  def orExpr: Parser[Or] = term ~ ("|" ~> term) ^^ { case phiL ~ phiR => Or(phiL, phiR) }
  //  def impliesExpr: Parser[Implies] = term ~ ("->" ~> term) ^^ { case phiL ~ phiR => Implies(phiL, phiR) }
  //
  //  def nextExpr: Parser[X] = next ~> term ^^ { phi => X(phi) }
  //  def eventuallyExpr: Parser[F] = "F" ~> term ^^ { phi => F(phi) }
  //  def alwaysExpr: Parser[G] = "G" ~> term ^^ { phi => G(phi) }
  //  def untilExpr: Parser[U] = term ~ ("U" ~> term) ^^ { case phiL ~ phiR => U(phiL, phiR) }
  //
  //  def parseLTL(str: String): ParseResult[LTL] = parseAll(expr, str)
  //  def apply(str: String): LTL = LTLParser.parseLTL(str) match {
  //    case LTLParser.Success(result: LTL, _) => result
  //    case _ => sys.error("Could not parse the input string: " + str)
  //  }
}
