import CauseBeer2011.causeApprox
import Lib.*
import Util.parseTraceFromPath

@main
def main(): Unit = {
  println(parseTraceFromPath("input-files/Beer2011/start_end_status_violation.txt"))
}