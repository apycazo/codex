package apycazo.codex.kotlin.basic

import java.util.*

data class UserInfo(
  val id:String,
  val name:String,
  val age:Int?,
  var active:Boolean,
  var role:String?) {

  /**
   * Create a builder using scope functions, in this case 'apply' who exposes the object as 'this'.
   * The scope functions available are let, run, with, apply, and also, with run, this and apply using
   * the param 'this' to access the referred instance, while let and also use 'it'.
   */
  data class Builder(
    val name:String,
    var age:Int? = null,
    var role:String? = null,
    var active:Boolean? = null) {
    fun age(value:Int) = apply { this.age = value }
    fun role(value:String) = apply { this.role = value }
    fun active(value:Boolean) = apply { this.active = value }
    fun build():UserInfo {
      return UserInfo(
        id = UUID.randomUUID().toString().substring(24),
        name = name,
        age = age ?: 0,
        active = active ?: false,
        role = role ?: "user"
      )
    }
  }
}