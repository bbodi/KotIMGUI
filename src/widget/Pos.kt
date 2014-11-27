package widget

abstract class Pos(val x: Int = 0, val y: Int = 0) {
	fun is_in_rect(topLeft: Pos, size: Pos) = (x >= topLeft.x) && (x <= topLeft.x + size.x) &&
			(y >= topLeft.y) && (y <= topLeft.y + size.y)
}

data class AbsolutePos(x: Int = 0, y: Int = 0) : Pos(x, y) {
	fun plus(v: Pos) = AbsolutePos(x + v.x, y + v.y)
	fun minus() = AbsolutePos(-x, -y)
	fun minus(v: AbsolutePos) = AbsolutePos(x - v.x, y - v.y)
	fun times(koef: Int) = AbsolutePos(x * koef, y * koef)

	fun add(relativePos: RelativePos, w: Int, h: Int): AbsolutePos {
		var pos = this
		if (Direction.LEFT in relativePos.dirs) {
			pos += AbsolutePos(relativePos.x, 0)
		} else if (Direction.RIGHT in relativePos.dirs) {
			pos += AbsolutePos(relativePos.x + w, 0)
		}
		if (Direction.DOWN in relativePos.dirs) {
			pos += AbsolutePos(0, relativePos.y + h)
		} else if (Direction.UP in relativePos.dirs) {
			pos += AbsolutePos(0, relativePos.y)
		}
		if (Direction.X_IS_ABSOLUTE in relativePos.dirs) {
			pos = AbsolutePos(relativePos.x, pos.y)
		}
		return pos
	}
}

enum class Direction {
	LEFT
	RIGHT
	DOWN
	UP
	X_IS_ABSOLUTE
	Y_IS_ABSOLUTE
}

data class RelativePos(x: Int = 0, y: Int = 0, val dirs: Array<Direction>) : Pos(x, y) {
}

public fun fromLastWidgetBottom(y: Int = 1): RelativePos = RelativePos(0, y, array(Direction.DOWN))
public fun fromLastWidgetBottomLeft(x: Int = 1, y: Int = 1): RelativePos = RelativePos(x, y, array(Direction.DOWN, Direction.LEFT))