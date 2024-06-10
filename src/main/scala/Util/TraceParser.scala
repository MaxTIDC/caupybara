package Util

import Lib.Execution

import scala.io.Source
import scala.util.matching.Regex

/**
 * Parse input file into internal representation of LTL traces.
 * Files containing multiple traces are supported.
 *
 * Pre:
 * Trace files formatted as in Buckworth et al. 2023
 * (see https://github.com/tbuckworth/Adapting-Specifications-for-Reactive-Controllers).
 */
def parseMultiTracesFromPath(path: String): Map[String, Execution] = {
  val fileSource = Source.fromFile(path)
  val patternGetName: Regex = """.*holds_at\([a-zA-Z].*,\s*[0-9]+,\s*([a-zA-Z].*)\)\.""".r

  // Get workingMap
  var workingMap = Map[String, Seq[String]]()

  for (line <- fileSource.getLines()) do line match
    case patternGetName(traceName) =>
      if !workingMap.contains(traceName) then
        workingMap += (traceName -> Seq())

      workingMap += (traceName -> (workingMap(traceName) :+ line))

    case _ => ; // pass

  // Parse each individual trace, collect to tracesMap
  workingMap.map((traceName, lines) => (traceName, parseTrace(lines)))
}

/**
 * Helper function that parses single trace (as sequence of strings).
 */
def parseTrace(lines: Seq[String]): Execution = {
  val patternTraceParse: Regex = """.*holds_at\(([a-zA-Z].*),\s*([0-9]+),.*\)\.""".r

  var trace = Map[Int, Set[String]]()

  for (line <- lines) do line match
    case patternTraceParse(atom, stateStr) =>
      val state = stateStr.toInt

      if !trace.contains(state) then
        trace += (state -> Set())

      if !(line contains "not_holds_at") then
        trace += (state -> (trace(state) + atom))

    case _ => ;  // pass

  trace
}
