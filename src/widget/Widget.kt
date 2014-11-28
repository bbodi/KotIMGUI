package widget

import timeline.widgetHandler

/*trait Widget {
	var pos: Pos
	var width: Int
	var height: Int
	fun draw()
	fun calcOwnSize()
	fun handleEvents()
}*/

data class PositionBasedId(val x: Int, val y: Int, val id: Int)

abstract class Widget(pos: Pos) {
	open var width: Int = 0
	open var height: Int = 0
	abstract val id: Int
	var parent: WidgetContainer? = null
	val pos = widgetHandler.getAbsolutePos(this, pos)

	abstract fun draw()
	abstract fun handleEvents()
	fun drawAndHandleEvents() {
		handleEvents()
		draw()
	}
}