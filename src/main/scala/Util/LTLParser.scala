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

trait LTLParserError
case class LTLLexerError(msg: String) extends LTLParserError
case class LTLTokenParserError(msg: String) extends LTLParserError

object LTLParser extends Parsers {
  // TODO
}

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
    phrase(rep1(lParen | rParen | X | G | F | U | not |
      and | or | iff | implies | t | f | atom)) ^^ identity
  }

  def apply(input: String):Either[LTLLexerError, List[LTLToken]] = {
    parse(tokens, input) match {
      case NoSuccess(msg, next) => Left(LTLLexerError(msg))
      case Success(result, next) => Right(result)
    }
  }
}

object LTLTokenParser extends Parsers {
  override type Elem = LTLToken

  class LTLTokenReader(tokensList: Seq[LTLToken]) extends Reader[LTLToken] {
    override def first: LTLToken = tokensList.head
    override def atEnd: Boolean = tokensList.isEmpty
    override def pos: Position = NoPosition
    override def rest: Reader[LTLToken] = new LTLTokenReader(tokensList.tail)
  }

  def truth: Parser[LTL] = TrueToken ^^ {_ => True}
  def falsity: Parser[LTL] = FalseToken ^^ { _ => False }
  def propAtom: Parser[Atom] = accept("atom", { case AtomToken(name) => Atom(name) })

  def nextTerm: Parser[X] = XToken ~> factor ^^ { phi => X(phi) }
  def eventuallyTerm: Parser[F] = FToken ~> factor ^^ { phi => F(phi) }
  def alwaysTerm: Parser[G] = GToken ~> factor ^^ { phi => G(phi) }
  def notTerm: Parser[Not] = NotToken ~> factor ^^ { case phi => Not(phi) }

  def andExpr: Parser[And] = term ~ (AndToken ~> term) ^^ { case phiL ~ phiR => And(phiL, phiR) }
  def orExpr: Parser[Or] = term ~ (OrToken ~> term) ^^ { case phiL ~ phiR => Or(phiL, phiR) }
  def impliesExpr: Parser[Implies] = term ~ (ImpliesToken ~> term) ^^ { case phiL ~ phiR => Implies(phiL, phiR) }
  def iffExpr: Parser[Iff] = term ~ (IffToken ~> term) ^^ { case phiL ~ phiR => Iff(phiL, phiR) }
  def untilExpr: Parser[U] = term ~ (UToken ~> term) ^^ { case phiL ~ phiR => U(phiL, phiR) }

  def bracketed: Parser[LTL] = LParen ~> expr <~ RParen ^^ identity

  def expr: Parser[LTL] = andExpr | orExpr | impliesExpr | iffExpr | untilExpr | term
  def term: Parser[LTL] = notTerm | nextTerm | eventuallyTerm | alwaysTerm | factor
  def factor: Parser[LTL] = propAtom | truth | falsity | bracketed

  //  def parseLTL(str: String): ParseResult[LTL] = parseAll(expr, str)
  //  def apply(str: String): LTL = LTLParser.parseLTL(str) match {
  //    case LTLParser.Success(result: LTL, _) => result
  //    case _ => sys.error("Could not parse the input string: " + str)
  //  }
  def apply(tokens: Seq[LTLToken]): Either[LTLTokenParserError, LTL] = {
    val reader = new LTLTokenReader(tokens)
    expr(reader) match {
      case NoSuccess(msg, next) => Left(LTLTokenParserError(msg))
      case Success(result, next) => Right(result)
    }
  }
}
