import CauseBeer2011.causeApprox
import Lib.*
import Util.*

object CausalityLTL {
  @main
  def main(psiStr: String, traceFilePath: String, others: String*): Unit = {
    try {
      val psi: LTL = toNNF(LTLParser(psiStr))  /** e.g. "G(h -> p) & G(m -> !p)" or "G((!req1 & !req2) | X ack)" */
      val trace: Trace = parseTraceFromPath(traceFilePath)  /** e.g. input-paths/Beer2011/req_ack_violation_1.txt */

      println(causeApprox(trace, 0, psi))
    } catch
//      case e: IllegalArgumentException => println("Format: java -jar fyp-causality.jar [LTL String] [Trace File Path]")
      case e: Exception => println("Error: " + e.getMessage)
  }
}