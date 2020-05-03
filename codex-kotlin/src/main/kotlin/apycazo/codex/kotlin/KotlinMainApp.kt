package apycazo.codex.kotlin

import jdk.jshell.spi.ExecutionControl

fun main() {
  val name = "kotlin-demo"
  var counter = 4
  counter++ // variables can change value
  println ("Name: $name, counter: $counter")
  var value:String? = null
  println ("Is value defined? ${value.isNullOrEmpty()} (${value ?: "empty!"})")
  value = "Not empty anymore"
  println ("Is value defined after being set? ${value.isNullOrEmpty()})")
  // kotlin has no checked exceptions!
  try {
    exceptional()
  } catch (e:Exception) {
    println("An exception was captured!: ${e.message}")
  } finally {
    println("This is always executed")
  }
}

/**
 * Nothing is a special marker, to indicate parts of the code that can never be reached
 */
fun exceptional(): Nothing {
  throw Exception("This method always return an exception")
}