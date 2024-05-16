package MainClass

import CauseBeer2011.causeApprox
import CauseMeng2024.Cause
import Lib.*
import Util.*
import upickle.default.*

object CausalityLTL {
  private val usage =
    """
    Usage: java -jar fyp-causality.jar [--ltl | -l LTL property string] [--trace | -t trace file path] [--cause | -c causality mode] [--out | -o output mode]
    """

  private def parseArgs(args: Array[String]): Map[String, String] = {
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

    var argMap = argMapBuilder.result()

    // Throw errors when crucial fields not included
    if !(argMap contains "psiStr") then throw sys.error("LTL property not specified!")
    if !(argMap contains "traceFilePath") then throw sys.error("Trace file not specified!")

    // Catch default options
    if !(argMap contains "outputMode") then argMap.+=("outputMode" -> "")
    if !(argMap contains "causeMode") then argMap.+=("causeMode" -> "meng2024")

    argMap
  }

  def main(args: Array[String]): Unit = {
    if args.isEmpty || args.length % 2 != 0 then
      println(usage)
      return

    val argMap = parseArgs(args)

    /** e.g. "G(h -> p) & G(m -> !p)" or "G((!req1 & !req2) | X ack)" */
    val psi: LTL = toNNF(LTLParser(argMap("psiStr")))
    /** e.g. input-files/Beer2011/req_ack_violation_1.txt */
    val trace: Trace = parseTraceFromPath(argMap("traceFilePath"))

    if argMap("causeMode") == "beer2011" || argMap("causeMode") == "beer" || argMap("causeMode") == "hana" then
      val cause = causeApprox(trace, 0, psi)
      argMap("outputMode") match
        case "pickle" | "pickled" | "p" => println(write(cause))
        case _ => println(cause)

    else if argMap("causeMode") == "meng2024" || argMap("causeMode") == "meng" || argMap("causeMode") == "max" then
      val cause = Cause.findViolationCauses(trace, 0, trace.size-1, psi)
      argMap("outputMode") match
        case "pickle" | "pickled" | "p" => println(write(cause))
        case _ => println(cause)

    else println("Please enter valid causality mode: beer2011 | meng2024.")
  }
}