package MainClass

import CauseBeer2011.{CausalPair, causeApprox}
import CauseNew.CausalSet
import CauseNew.Cause.findViolationCauses
import Lib.*
import Util.*
import upickle.default.*

object CausalityLTL {
  private val DEFAULT_BOUND_STR = "5"
  private val DEFAULT_CAUSE_MODE = "new"
  private val usage =
    """
    Usage: caupybara [--ltl | -l] [--trace | -t] ([--cause | -c] [--bound | -b])
    """

  private def isCauseBeer2011(mode: String): Boolean = {
    mode == "beer" || mode == "beer2011"
  }

  private def isCauseNew(mode: String): Boolean = {
    mode == "new"
  }

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
    if !(argMap contains "boundStr") then argMap += ("boundStr" -> DEFAULT_BOUND_STR)
    if !(argMap contains "causeMode") then argMap += ("causeMode" -> DEFAULT_CAUSE_MODE)

    // Throw errors when crucial fields not included
    if !(argMap contains "psiStr") then throw sys.error("LTL property not specified!")
    if !(argMap contains "traceFilePath") then throw sys.error("Trace file not specified!")

    if !isCauseBeer2011(argMap("causeMode")) && !isCauseNew(argMap("causeMode")) then
      throw sys.error("Please enter valid causality mode: beer | new.")

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
     *  - Minepump: "G(HighWater -> X(Pump)) & G(Methane -> X(!Pump))"
     */
    val property: LTL = LTLParser(argMap("psiStr"))
    val traceMap: Map[String, Execution] = parseMultiTracesFromPath(argMap("traceFilePath"))
    val bound = argMap("boundStr").toInt

    // Compute and print causes
    if isCauseBeer2011(argMap("causeMode")) then
      val computedCauses: Map[String, Set[CausalPair]] = traceMap.map(
        (name, trace) => (name, causeApprox(trace, 0, NNFConverter.toNNF(property, beer2011 = true)))
      )
      print(write(computedCauses))

    else if isCauseNew(argMap("causeMode")) then
      val computedCauses: Map[String, Set[CausalSet]] = traceMap.map(
        (name, trace) => (name, findViolationCauses(trace, 0, trace.size - 1, NNFConverter.toNNF(property), bound))
      )
      print(write(computedCauses))
  }
}