package apycazo.codex.kotlin.basic

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Basics {

  @Test
  fun safeAccessors() {
    val head = DataNode("this", null)
    assertThat(head.next?.text).isNull()
    head.next = DataNode("is", DataNode("sparta!", null))
    assertThat(head.concatenate(" ")).isEqualTo("this is sparta!")
  }

  @Test
  fun elvisOperator() {
    var value:String? = null
    assertThat(value ?: "empty").isEqualTo("empty")
    value = "elvis"
    assertThat(value ?: "empty").isEqualTo("elvis")
  }
}