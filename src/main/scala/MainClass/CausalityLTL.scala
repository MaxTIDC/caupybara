package MainClass

import CauseBeer2011.{CausalPair, causeApprox}
import CauseMeng2024.CausalSet
import CauseMeng2024.Cause.findViolationCauses
import Lib.*
import Util.*
import upickle.default.*

object CausalityLTL {
  private val usage =
    """
    Usage: caupybara [--ltl | -l LTL property string] [--trace | -t trace file path] [--cause | -c causality mode] [--out | -o output mode]
    """

  /**
   * Parse input arguments.
   */
  private def parseArgs(args: Array[String]): Map[String, String] = {
    var argMap = Map[String, String]()
    args.sliding(2, 2).toList.collect {
      case Array("--ltl", psiStr: String) => argMap += ("psiStr" -> psiStr)
      case Array("-l", psiStr: String) => argMap += ("psiStr" -> psiStr)

      case Array("--trace", traceFilePath: String) => argMap += ("traceFilePath" -> traceFilePath)
      case Array("-t", traceFilePath: String) => argMap += ("traceFilePath" -> traceFilePath)

      case Array("--cause", causeMode: String) => argMap += ("causeMode" -> causeMode.toLowerCase)
      case Array("-c", causeMode: String) => argMap += ("causeMode" -> causeMode.toLowerCase)

      case Array("--out", outputMode: String) => argMap += ("outputMode" -> outputMode.toLowerCase)
      case Array("-o", outputMode: String) => argMap += ("outputMode" -> outputMode.toLowerCase)
    }

    // Catch default options
    if !(argMap contains "outputMode") then argMap.+=("outputMode" -> "")
    if !(argMap contains "causeMode") then argMap.+=("causeMode" -> "meng2024")

    // Throw errors when crucial fields not included
    if !(argMap contains "psiStr") then throw sys.error("LTL property not specified!")
    if !(argMap contains "traceFilePath") then throw sys.error("Trace file not specified!")

    if !(argMap("causeMode") contains "beer") && !(argMap("causeMode") contains "meng") then
      throw sys.error("Please enter valid causality mode: beer2011 | meng2024.")

    argMap
  }

  /**
   * Main function.
   */
  def main(args: Array[String]): Unit = {
    if args.isEmpty || args.length % 2 != 0 then
      println(usage)
      return

    val argMap = parseArgs(args)

    /** e.g. "G(h -> p) & G(m -> !p)" or "G((!req1 & !req2) | X ack)" */
    val psi: LTL = toNNF(LTLParser(argMap("psiStr")))
    val traceMap: Map[String, Trace] = parseMultiTracesFromPath(argMap("traceFilePath"))

    // Compute and print causes
    if argMap("causeMode") == "beer2011" || argMap("causeMode") == "beer" then
      val computedCauses: Map[String, Set[CausalPair]] =
        traceMap.map((name, trace) => (name, causeApprox(trace, 0, psi)))

      argMap("outputMode") match
        case "original" => print(computedCauses.toString())
        case _ => print(write(computedCauses))

    else if argMap("causeMode") == "meng2024" || argMap("causeMode") == "meng" then
      val computedCauses: Map[String, Set[CausalSet]] =
        traceMap.map((name, trace) => (name, findViolationCauses(trace, 0, trace.size - 1, psi)))

      argMap("outputMode") match
        case "original" => print(computedCauses.toString())
        case _ => print(write(computedCauses))
  }
}