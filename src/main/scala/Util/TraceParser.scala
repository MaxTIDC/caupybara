package Util

import Lib.Trace

import scala.collection.mutable
import scala.io.Source
import scala.util.matching.Regex

def parseTraceFromPath(path: String): Trace = {
  val pattern: Regex = """.*holds_at\(([a-zA-Z0-9]+),([0-9]+),.*\)\.""".r

  val trace = mutable.Map[Int, Set[String]]()
  val fileSource = Source.fromFile(path)

  for (line <- fileSource.getLines()) { line match
    case pattern(atom, stateStr) => {
      val state = stateStr.toInt

      if !trace.contains(state) then
        trace += (state -> Set())

      if !(line contains "not_holds_at") then
        val updatedSet = trace(state) + atom
        trace.update(state, updatedSet)
    }
    case _ => ;  // pass
  }

  trace.toMap
}
