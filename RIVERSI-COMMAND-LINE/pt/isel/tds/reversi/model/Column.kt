package pt.isel.tds.reversi.model

import java.lang.IllegalArgumentException


class Column private constructor(val char: Char){
    val symbol = char
    val index = char - 'A'
    override fun toString() = "Column $char"
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Column) {
            return false
        }
        return this.char == other.char && this.index == other.index
    }
    companion object{
        val values = List(BOARD_DIM) { Column('A' + it) }
        operator fun invoke(char: Char) = run {
            require(char in 'A'..'A'+BOARD_DIM)
            values[char - 'A']
        }
    }
}

fun Char.toColumnOrNull(): Column? = if (this in 'A'..'H') Column.values[this - 'A'] else null
fun Char.toColumn():Column = run{
    require(this in 'A'..'A'+ BOARD_DIM){throw IllegalArgumentException("Invalid column $this")}
    Column.values[this - 'A']
}