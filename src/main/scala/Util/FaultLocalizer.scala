package Util

import Lib.*

def localizeFaults(trace: Trace, psi: LTL): Set[(Int, Int)] = {
  var i = 0
  var j = 0
  var isFaulty = false

  var faults: Set[(Int, Int)] = Set()

  while j < trace.size do
    if evalTrilean(trace, i, j, psi) != Trilean.F then
      if isFaulty then
        faults += (i-1, j)
        isFaulty = false
      j += 1
    else
      isFaulty = true
      i += 1

  faults
}
