package widget

class Pos(val x: Int = 0, val y: Int = 0) {
	fun isInRect(topLeft: Pos, size: Pos) = (x >= topLeft.x) && (x < topLeft.x + size.x) &&
			(y >= topLeft.y) && (y < topLeft.y + size.y)
	fun plus(v: Pos) = Pos(x + v.x, y + v.y)
	fun minus() = Pos(-x, -y)
	fun minus(v: Pos) = Pos(x - v.x, y - v.y)
	fun times(koef: Int) = Pos(x * koef, y * koef)
}

enum class Direction {
	LEFT
	RIGHT
	DOWN
	UP
	X_IS_ABSOLUTE
	Y_IS_ABSOLUTE
}

data class Rect(val x: Int, val y: Int, val width: Int, val height: Int) {
	class object {
		fun fromPositions(pos: Pos, pos2: Pos): Rect {
			val x1 = Math.min(pos.x, pos2.x)
			val x2 = Math.max(pos.x, pos2.x)
			val y1 = Math.min(pos.y, pos2.y)
			val y2 = Math.max(pos.y, pos2.y)
			return Rect(x1, y1, x2-x1, y2-y1)
		}
	}
	val topLeft: Pos
		get() = Pos(x, y)
	val bottomRight: Pos
		get() = Pos(x+width, y + height)
}