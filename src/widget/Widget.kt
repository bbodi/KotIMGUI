package widget

import timeline.app

/*trait Widget {
	var pos: AbsolutePos
	var width: Int
	var height: Int
	fun draw()
	fun calcOwnSize()
	fun handleEvents()
}*/

data class PositionBasedId(val x: Int, val y: Int, val id: Any) {}

abstract class Widget(pos: Pos) {
	open var width: Int = 0
	open var height: Int = 0

	var parent: WidgetContainer? = null
	val pos = pos
	var additionalIdInfo: String = ""
	val id: Int
		get() = PositionBasedId(this.pos.x, this.pos.y, additionalIdInfo.hashCode()).hashCode()

	abstract fun draw()
	abstract fun handleEvents()
	fun drawAndHandleEvents() {
		handleEvents()
		draw()
	}

	fun toRight(x: Int = 1): Pos {
		return Pos(this.pos.x + this.width + x, this.pos.y)
	}
}