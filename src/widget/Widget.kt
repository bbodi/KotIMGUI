package widget

/*trait Widget {
	var pos: Pos
	var width: Int
	var height: Int
	fun draw()
	fun calcOwnSize()
	fun handleEvents()
}*/

private data class PositionBasedId(val x: Int, val y: Int, val id: Int)

abstract class Widget(val widgetHandler: WidgetHandler) {
	var pos: Pos = AbsolutePos(0, 0)
	open var width: Int = 0
	open var height: Int = 0
	abstract val id: Int

	abstract fun draw()
	abstract fun handleEvents()
	fun drawAndHandleEvents() {
		handleEvents()
		draw()
	}
}