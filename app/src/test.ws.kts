val array = arrayOf(1, 2, 4, 9, 5)
println(array.size)
println()
array.forEach {
    println(it)
}

array.forEachIndexed { i, j ->
    println("i=$i,j=$j")
}

array.maxOfOrNull {
    it > 0
}