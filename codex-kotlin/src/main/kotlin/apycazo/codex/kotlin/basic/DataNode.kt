package apycazo.codex.kotlin.basic

data class DataNode(
  val text:String,
  var next:DataNode?) {

  fun concatenate(separator:String):String {
    var fullText = text
    var cursor = next
    while(cursor != null) {
      fullText += "$separator${cursor.text}"
      cursor = cursor.next
    }
    return fullText
  }
}