import java.net.URI

val link = "https://www.baidu.com/img/PCfb_5bf082d29588c07f842ccde3f97243ea.png"

val lastDot = link.lastIndexOf(".")
val fileType = link.substring(lastDot)
println(fileType)
val lastSlash = link.lastIndexOf("/")
val fileName = link.substring(lastSlash + 1, lastDot)
println(fileName)

val uri = URI.create(link)

uri.path
uri.host
uri.scheme
