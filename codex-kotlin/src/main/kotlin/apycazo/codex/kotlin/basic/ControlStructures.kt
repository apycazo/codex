package apycazo.codex.kotlin.basic

class ControlStructures {

  /**
   * If structure demonstrator 1
   */
  fun returnGreaterOf(a:Int, b:Int): Int {
    return if (a >= b) a else b
  }

  /**
   * If structure: this one warns us that the 'return' should be lifted.
   */
  fun returnGreaterPlusDelta(a:Int, b:Int, delta:Int): Int {
    if (a >= b) {
      return a + delta
    } else {
      return b + delta
    }
  }

  /**
   * While structure
   */
  fun whileOddSumValue(values:List<Int>):Int {
    if (values.isEmpty()) return 0
    var isOdd = true
    var index = 0
    var accumulator = 0
    while (isOdd && index <= values.size) {
      val value = values[index]
      isOdd = value % 2 == 0
      if (isOdd) accumulator += value
      index++
    }
    return accumulator
  }

  /**
   * For structure. The 'value' can include type, like 'for(value:Int in values)'
   */
  fun forAllElements(values:List<Int>):Int {
    var accumulator = 0
    for (value in values) {
      accumulator += value
    }
    return accumulator
  }

  /**
   * For structure, including index on each pass.
   */
  fun forWithIndex(values:List<Int>):Int {
    var accumulator = 0
    for ((i,v) in values.withIndex()) {
      if (i % 2 == 0) accumulator += v
    }
    return accumulator
  }

  /**
   * Iterate over the value indexes only.
   */
  fun forIteratingIndex(values:List<Int>):Int {
    var accumulator = 0
    for (i in values.indices) {
      accumulator += i
    }
    return accumulator
  }

  /**
   * When structure. Each case can use a block '{}' structure too.
   */
  fun whenExpressions(value:Any?):String {
    return when(value) {
      null -> "value is null"
      is String -> "value was '$value'"
      1 -> "one"
      2 -> "two"
      in 10..20 -> "between 10 and 20"
      else -> "unknown"
    }
  }
}