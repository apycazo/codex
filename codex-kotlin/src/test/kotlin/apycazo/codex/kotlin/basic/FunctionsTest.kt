package apycazo.codex.kotlin.basic

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Internal is equivalent to java 'package' visibility.
 */
internal class FunctionsTest {

  // this variable can not be null, but will be initialized later
  private lateinit var target:Functions

  @BeforeEach
  fun init() {
    target = Functions("demo")
  }

  @Test
  @DisplayName("simple increment")
  fun incrementingValueReturnsExpected() {
    target.increment()
    Assertions.assertThat(target.getCounter()).isEqualTo(1)
  }

  @Test
  @DisplayName("delta increment")
  fun deltaIncrementReturnsExpected() {
    target.delta()
    Assertions.assertThat(target.getCounter()).isEqualTo(1)
    target.delta(9)
    Assertions.assertThat(target.getCounter()).isEqualTo(10)
    target.delta(-5)
    Assertions.assertThat(target.getCounter()).isEqualTo(5)
  }

  @Test
  @DisplayName("show of scope functions")
  fun useScopeFunctions() {
    target = Functions("john").also {
      // scoped also uses 'it' like a 'this' pointer.
      println ("Name: ${it.name}, Counter: ${it.getCounter()}")
      it.rename("mutated")
      it.setValue(100)
      println ("Name: ${it.name}, Counter: ${it.getCounter()}")
    }
    Assertions.assertThat(target.getCounter()).isEqualTo(100)
    Assertions.assertThat(target.name).isEqualTo("mutated")
  }

  @Test
  @DisplayName("call to inline functions")
  fun inlineFunctionsAndMethods() {
    val sum = target.absoluteIncrementAndSum(-5, 10)
    Assertions.assertThat(sum).isEqualTo(17)
  }

  @Test
  @DisplayName("classes can use extended methods")
  fun extensionUsage() {
    val list = target.getInitializedList(5)
    Assertions.assertThat(list).hasSize(5)
    Assertions.assertThat(list).containsAll(listOf(1,2,3,4,5))
  }

  @Test
  @DisplayName("call of function returned as a response")
  fun callFunctionResponse() {
    val sum = target.absoluteDeltaPlusValue(-5).invoke(5)
    Assertions.assertThat(sum).isEqualTo(10)
  }
}