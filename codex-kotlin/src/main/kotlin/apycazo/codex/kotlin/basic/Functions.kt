package apycazo.codex.kotlin.basic

import java.util.concurrent.atomic.AtomicInteger

class Functions(var name:String) {

  /**
   * Declaration and initialization (not a constructor value)
   */
  private val counter:AtomicInteger = AtomicInteger(0)

  /**
   * Since counter is private, we need a custom function to access it.
   */
  fun getCounter():Int {
    return counter.get()
  }

  /**
   * Shows a simple function, increasing the counter value. To return
   * void types in kotlin we need to add '?' to the 'Void' type so we
   * can return null.
   */
  fun increment():Void? {
    counter.incrementAndGet()
    return null // we need to return a null value for 'void' responses
  }

  /**
   * Function with a single param, providing a default value fro it.
   * Notice that we don't really need to return anything.
   */
  fun delta(inc:Int = 1) {
    counter.addAndGet(inc)
  }

  /**
   * Simple function just to change an initialization variable.
   */
  fun rename(newName:String) {
    this.name = newName
  }

  /**
   * Simple function to set the initial value.
   */
  fun setValue(newValue:Int) {
    this.counter.set(newValue)
  }

  /**
   * Shows definition and usage of inline lambda functions.
   */
  fun absoluteIncrementAndSum(a:Int, b:Int):Int {
    // function with type definitions
    val absolute = fun(a:Int):Int { return if (a >= 0) a else -a }
    // simple consumer function can be event simpler
    val increment = { value:Int -> value+1 }
    // run operation
    return increment(absolute(a)) + increment(absolute(b))
  }

  /**
   * This function extends mutableList<Int>, and is resolved STATICALLY.
   * These functions must be prepended with the type they extend like this:
   */
  private fun MutableList<Int>.initMe(value:Int) {
    if (value > 0) {
      for (i in 1..value) add(i)
    }
  }

  /**
   * This function makes use of the 'initMe' extension, defined above.
   */
  fun getInitializedList(value:Int):MutableList<Int> {
    val list = mutableListOf<Int>()
    list.initMe(value)
    return list
  }

  /**
   * Functions can be returned as results, and called with 'invoke'
   */
  fun absoluteDeltaPlusValue(delta:Int):(Int) -> Int {
    val absDelta = if (delta < 0) -delta else delta
    return fun(v:Int):Int { return v + absDelta }
  }
}