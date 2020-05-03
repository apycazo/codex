package apycazo.codex.kotlin.basic

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class InfoResourceTest {

  @Test
  @DisplayName("data class can be created and accessed easily")
  fun testInfoResourceInstance() {
    val info = InfoResource(name = "sample", data = 100)
    assertThat(info).isNotNull
    assertThat(info.name).isEqualTo("sample")
    assertThat(info.data).isEqualTo(100)
    println (info)
  }

}