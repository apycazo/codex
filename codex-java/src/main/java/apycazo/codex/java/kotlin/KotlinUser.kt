package apycazo.codex.java.kotlin

data class KotlinUser(val user:String, val id:Int, val enabled:Boolean = true) {

  constructor(user:String, id:Int): this(user, id, true)

  fun withEnabled(isEnabled:Boolean): KotlinUser {
    return this.copy(enabled = true)
  }
}