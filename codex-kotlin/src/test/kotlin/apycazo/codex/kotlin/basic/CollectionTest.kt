package apycazo.codex.kotlin.basic

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class CollectionTest {

  @Test
  @DisplayName("create and operate with arrays")
  fun createAndOperateWithArrays() {
    // -- create an array
    val data = arrayOf(1,2,3)
    assertThat(data).hasSize(3)
    assertThat(data[0]).isEqualTo(1)
    // -- mutate the array
    data[1] = 10
    assertThat(data[1]).isEqualTo(10)
  }

  @Test
  @DisplayName("create and operate with lists")
  fun createAndOperateWithLists() {
    val list = listOf(1,2,3)
    assertThat(list).hasSize(3)
    assertThat(list[0]).isEqualTo(1)
    assertThat(list.contains(2)).isTrue()
    // -- to mutate a list we need to do it explicitly
    var mutableList = list.toMutableList()
    mutableList.add(4)
    // -- check that we have a copy of the list
    assertThat(list).hasSize(3)
    assertThat(mutableList).hasSize(4)
    assertThat(mutableList[3]).isEqualTo(4)
    // -- we can use optionals here
    var testValue = mutableList.getOrElse(10) { index -> 100 + index }
    assertThat(testValue).isEqualTo(110)
    // -- recreate the list updating all values
    mutableList = mutableList.map { value -> value *2 }.toMutableList()
    mutableList.forEach { println ("value: $it") }
    testValue = mutableList.getOrElse(10) { index -> 100 + index }
    assertThat(testValue).isEqualTo(110)
    assertThat(mutableList[1]).isEqualTo(4)
    // -- iterate, modifying the list duplicating only odd values
    mutableList = mutableListOf(1,2,3)
    val iterator = mutableList.listIterator()
    while (iterator.hasNext()) {
      val currentValue = iterator.next()
      if (currentValue % 2 == 0) iterator.set(currentValue * 2)
    }
    assertThat(mutableList[0]).isEqualTo(1)
    assertThat(mutableList[1]).isEqualTo(4)
    assertThat(mutableList[2]).isEqualTo(3)
  }

  @Test
  @DisplayName("create and operate with lists")
  fun createAndOperateWithMaps() {
    val map = mapOf("john" to true, "jane" to false)
    assertThat(map["john"]).isTrue()
    assertThat(map["gandalf"]).isNull() // default value is null
    assertThat(map.getOrElse("gandalf") {false}).isFalse()
    val mutableMap = map.toMutableMap()
    mutableMap["jake"] = true
    assertThat(mutableMap["jake"]).isTrue()
    val validator = fun(key:String, value:Boolean) {
      when (key) {
        "john" -> assertThat(value).isTrue()
        "jane" -> assertThat(value).isFalse()
      }
    }
    // -- iterate map over indexes
    for ((k,v) in map) { validator(k,v) }
    // -- other form of iteration
    map.forEach { (k, v) -> validator(k,v) }
    // -- accessing keys, values and complete entries
    map.keys.forEach { key -> println ("key: $key") }
    map.values.forEach { key -> println ("value: $key") }
    map.entries.forEach { entry -> println("Entry: {${entry.key}:${entry.value}}") }
  }

  @Test
  @DisplayName("collections can use string joins")
  fun stringJoinElements() {
    val list = listOf("this", "is", "sparta")
    val msg = list.joinToString(separator = " ", prefix = "¡", postfix = "!")
    assertThat(msg).isEqualTo("¡this is sparta!")
  }
}