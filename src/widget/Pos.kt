package widget

// TODO, egyelőre így marad, de ha később sem lesz szükség erre a megkülönböztetésre, akkor legyen csak simán Pos.
// A panelban minden posiziót relatívnak veszek
abstract data class Pos(val x: Int = 0, val y: Int = 0) {
	fun is_in_rect(topLeft: Pos, size: Pos) = (x >= topLeft.x) && (x <= topLeft.x + size.x) &&
			(y >= topLeft.y) && (y <= topLeft.y + size.y)
}

data class AbsolutePos(x: Int = 0, y: Int = 0) : Pos(x, y) {
	fun plus(v: Pos) = AbsolutePos(x + v.x, y + v.y)
	fun minus() = AbsolutePos(-x, -y)
	fun minus(v: AbsolutePos) = AbsolutePos(x - v.x, y - v.y)
	fun times(koef: Int) = AbsolutePos(x * koef, y * koef)


}

enum class Direction {
	LEFT
	RIGHT
	DOWN
	UP
}

class RelativePos(x: Int = 0, y: Int = 0, val dirs: Array<Direction>) : Pos(x, y) {
	fun plus(v: Pos) = RelativePos(x + v.x, y + v.y, dirs)
	fun minus() = RelativePos(-x, -y, dirs)
	fun minus(v: Pos) = RelativePos(x - v.x, y - v.y, dirs)
	fun times(koef: Int) = RelativePos(x * koef, y * koef, dirs)

	fun calcAbsolutePosFrom(from: AbsolutePos, w: Int, h: Int): AbsolutePos {
		var pos = from
		if (Direction.LEFT in dirs) {
			pos -= AbsolutePos(this.x, 0)
		} else if (Direction.RIGHT in dirs) {
			pos += AbsolutePos(this.x + w, 0)
		}
		if (Direction.DOWN in dirs) {
			pos += AbsolutePos(0, this.y + h)
		} else if (Direction.UP in dirs) {
			pos -= AbsolutePos(0, this.y)
		}
		return pos
	}
}

public fun downFromLastWidget(y: Int = 1): RelativePos = RelativePos(0, y, array(Direction.DOWN))