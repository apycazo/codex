package apycazo.codex.kotlin.basic

import java.util.*

data class InfoResource(
  val id:String = UUID.randomUUID().toString().substring(24),
  val name:String,
  var data:Any? // kotlin does not have a generic 'object', instead uses 'Any'
)