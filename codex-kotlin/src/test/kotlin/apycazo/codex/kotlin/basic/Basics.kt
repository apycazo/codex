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

  @Test
  fun builderPattern() {
    // using only default and required values
    var userInfo = UserInfo.Builder("john").build()
    assertThat(userInfo).isNotNull()
    assertThat(userInfo.id).isNotEmpty()
    assertThat(userInfo.name).isEqualTo("john")
    assertThat(userInfo.age).isEqualTo(0)
    assertThat(userInfo.role).isEqualTo("user")
    assertThat(userInfo.active).isFalse()
    // using custom values
    userInfo = UserInfo.Builder("jane")
      .age(25).active(true).role("admin")
      .build()
    assertThat(userInfo).isNotNull()
    assertThat(userInfo.id).isNotEmpty()
    assertThat(userInfo.name).isEqualTo("jane")
    assertThat(userInfo.age).isEqualTo(25)
    assertThat(userInfo.role).isEqualTo("admin")
    assertThat(userInfo.active).isTrue()
  }

}