package pt.isel.tds.reversi.model


class Row private constructor(val n: Int) {
    val number = n
    val index = n - 1
    override fun toString() = "Row $n"
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Row) {
            return false
        }
        return this.n == other.n && this.index == other.index
    }
    companion object {
        val values = List(BOARD_DIM) { Row(it + 1) }
        operator fun invoke(n: Int) = run {
            require(n in 1 .. BOARD_DIM)
            values[n - 1]
        }
    }
}

fun Int.toRowOrNull(): Row? = if (this in 1 .. BOARD_DIM) Row.values[this - 1] else null
fun Int.toRow(): Row = run{
    require(this in 1 .. BOARD_DIM){throw IllegalArgumentException("Invalid row $this")}
    Row.values[this - 1]
}
