import CauseBeer2011.causeApprox
import Lib.*
import Util.parseTraceFromPath

@main
def main(): Unit = {
  println(parseTraceFromPath("input-files/Beer2011/req_ack_violation.txt"))
}