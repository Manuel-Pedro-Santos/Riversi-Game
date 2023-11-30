package pt.isel.tds.reversi.model
import java.lang.IllegalArgumentException


enum class Direction(val difRow: Int, val difCol: Int) {
    UP(-1,0), DOWN(1,0), LEFT(0,-1),
    RIGHT(0,1), UP_LEFT(-1,-1),
    UP_RIGHT(-1,1), DOWN_LEFT(1,-1), DOWN_RIGHT(1,1)
}

class Cell private constructor(val row: Row, val col: Column) {
    val rowIndex = row.index
    val colIndex = col.index
    override fun toString(): String = "${row.n}${col.char}"

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Cell) {
            return false
        }
        return this.row == other.row && this.col == other.col
    }
    companion object{
        val values = List(BOARD_DIM * BOARD_DIM) { Cell(Row.values[it / BOARD_DIM], Column.values[it % BOARD_DIM]) }
        val INVALID =  Cell(-1, -1)
        operator fun invoke(row: Row, col: Column) = run {
            require(row in Row.values && col in Column.values)
            values[row.index * BOARD_DIM + col.index]
        }
    }

}
fun Cell(row: Int, col: Int): Cell =
    if (row in Row.values.indices && col in Column.values.indices) {
        Cell.values[row * BOARD_DIM + col]
    } else {
        Cell.INVALID
    }


fun String.toCellOrNull(): Cell? = run{
    if (this.length == 2){
        val row = this[0].toString().toIntOrNull()?.toRowOrNull()
        val col = this[1].toColumnOrNull()
        if (row != null && col != null) Cell.values[row.index * BOARD_DIM + col.index] else null
    } else null
}


fun String.toCell(): Cell = run{
    require(this.length == 2){throw IllegalArgumentException("Cell must have row and column")}
    val row = this[0].toString().toInt().toRow()
    val col = this[1].toColumn()
    Cell.values[row.index * BOARD_DIM + col.index]
}

operator fun Cell.plus(dir: Direction) = Cell(this.rowIndex + dir.difRow, this.colIndex + dir.difCol)

fun cellsInDirection(from: Cell, dir: Direction): List<Cell> {
    return generateSequence(from + dir) { prev -> prev + dir }
        .takeWhile { it != Cell.INVALID }
        .toList()
}


fun Int.toCellOrNull(): Cell? = run{
    if (this in Cell.values.indices) Cell.values[this] else null
}
fun Int.toCell() = checkNotNull(toCellOrNull())
operator fun Cell.minus(other: Cell): Direction = Direction.values().first { it.difRow == other.rowIndex - rowIndex && it.difCol == other.colIndex - colIndex }

operator fun Cell.minus(dir: Direction) = Cell(this.rowIndex - dir.difRow, this.colIndex - dir.difCol)
