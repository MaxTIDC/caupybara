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
    Usage: caupybara [--ltl | -l LTL property string] [--trace | -t trace file path] ([--cause | -c causality mode] [--bound | -b])
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

      case Array("--bound", boundStr: String) => argMap += ("boundStr" -> boundStr)
      case Array("-b", boundStr: String) => argMap += ("boundStr" -> boundStr)
    }

    // Catch default options
    //    if !(argMap contains "outputMode") then argMap.+=("outputMode" -> "")
    if !(argMap contains "boundStr") then argMap.+=("boundStr" -> "5")
    if !(argMap contains "causeMode") then argMap.+=("causeMode" -> "meng2024")

    // Throw errors when crucial fields not included
    if !(argMap contains "psiStr") then throw sys.error("LTL property not specified!")
    if !(argMap contains "traceFilePath") then throw sys.error("Trace file not specified!")

    if !(argMap("causeMode") contains "beer") && !(argMap("causeMode") contains "meng") then
      throw sys.error("Please enter valid causality mode: beer2011 | meng2024.")

    if !argMap("boundStr").forall(_.isDigit) then
      throw sys.error("Bound " + argMap("boundStr") + " not an integer.")

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

    /** Motivating examples:
     *  - The request-acknowledge system: "G((!req1 & !req2) | X ack)"
     *  - Minepump: "G(highwater -> X(pump)) & G(methane -> X(!pump))"
     */
    val property: LTL = LTLParser(argMap("psiStr"))
    val traceMap: Map[String, Execution] = parseMultiTracesFromPath(argMap("traceFilePath"))
    val bound = argMap("boundStr").toInt

    // Compute and print causes
    if argMap("causeMode") == "beer2011" || argMap("causeMode") == "beer" then
      val computedCauses: Map[String, Set[CausalPair]] = traceMap.map(
        (name, trace) => (name, causeApprox(trace, 0, NNFConverter.toNNF(property, beer2011 = true)))
      )
      print(write(computedCauses))

    else if argMap("causeMode") == "meng2024" || argMap("causeMode") == "meng" then
      val computedCauses: Map[String, Set[CausalSet]] = traceMap.map(
        (name, trace) => (name, findViolationCauses(trace, 0, trace.size - 1, NNFConverter.toNNF(property), bound))
      )
      print(write(computedCauses))
  }
}