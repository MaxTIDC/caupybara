package MainClass

import CauseBeer2011.causeApprox
import CauseHerong2024.Cause
import Lib.*
import upickle.default._
import Util.*

object CausalityLTL {
  private val usage =
    """
    Usage: java -jar fyp-causality.jar [--ltl | -l LTL property string] [--trace | -t trace file path] [--cause | -c causality mode] [--out | -o output mode]
    """

  def main(args: Array[String]): Unit = {
    if args.isEmpty || args.length % 2 != 0 then
      println(usage)
    else
      val argMapBuilder = Map.newBuilder[String, String]
      args.sliding(2, 2).toList.collect {
        case Array("--ltl", psiStr: String) => argMapBuilder.+=("psiStr" -> psiStr)
        case Array("-l", psiStr: String) => argMapBuilder.+=("psiStr" -> psiStr)

        case Array("--trace", traceFilePath: String) => argMapBuilder.+=("traceFilePath" -> traceFilePath)
        case Array("-t", traceFilePath: String) => argMapBuilder.+=("traceFilePath" -> traceFilePath)

        case Array("--cause", causeMode: String) => argMapBuilder.+=("causeMode" -> causeMode.toLowerCase)
        case Array("-c", causeMode: String) => argMapBuilder.+=("causeMode" -> causeMode.toLowerCase)

        case Array("--out", outputMode: String) => argMapBuilder.+=("outputMode" -> outputMode.toLowerCase)
        case Array("-o", outputMode: String) => argMapBuilder.+=("outputMode" -> outputMode.toLowerCase)
      }

      val argMap = argMapBuilder.result()
      /** e.g. "G(h -> p) & G(m -> !p)" or "G((!req1 & !req2) | X ack)" */
      val psi: LTL = toNNF(LTLParser(argMap("psiStr")))
      /** e.g. input-files/Beer2011/req_ack_violation_1.txt */
      val trace: Trace = parseTraceFromPath(argMap("traceFilePath"))

      if argMap("causeMode") == "beer2011" || argMap("causeMode") == "beer" || argMap("causeMode") == "hana" then
        val cause = causeApprox(trace, 0, psi)
        argMap("outputMode") match
          case "pickle" | "pickled" | "p" => println(write(cause))
          case _ => println(cause)

      else if argMap("causeMode") == "herong2024" || argMap("causeMode") == "herong" || argMap("causeMode") == "max" then
        val cause = Cause.findViolationCauses(trace, 0, trace.size-1, psi)
        argMap("outputMode") match
          case "pickle" | "pickled" | "p" => println(write(cause))
          case _ => println(cause)

      else println("Please enter valid causality mode: beer2011 | herong2024.")
  }
}