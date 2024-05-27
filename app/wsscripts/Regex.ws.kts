import java.util.Locale

val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[a-z]{2,6}")

val text = "Please contact us at support@example.com or sales@example.com."
println(emailRegex.matches(text))
println(emailRegex.matches("yingkongshi11@gmail.com"))

val matchResult = emailRegex.find(text)
println("matchResult ${matchResult?.value}")
println("group ${matchResult?.groups}")
println(matchResult?.groups?.get(0))


println()
val matchAllResult = emailRegex.findAll(text)
matchAllResult.forEach {
    println(it.value)
    println(it.range)
    println(it.groups)
    println(it.groupValues)

    println("=========================")
}

val newResult = emailRegex.replace(text, "O(∩_∩)O哈哈~")
println(newResult)
val newnewResult = emailRegex.replace(text) { r ->
    val sb = StringBuilder()
    sb.append(r.value.uppercase(Locale.getDefault()))
    sb
}
println(newnewResult)
val result1 = emailRegex.replaceFirst(text, "_(:з」∠)_ogo")
println(result1)
