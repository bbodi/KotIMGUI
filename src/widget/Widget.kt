package widget

/*trait Widget {
	var pos: Pos
	var width: Int
	var height: Int
	fun draw()
	fun calcOwnSize()
	fun handleEvents()
}*/


abstract class Widget(val widgetHandler: WidgetHandler) {
	var pos: Pos = AbsolutePos(0, 0)
	open var width: Int = 0
	open var height: Int = 0

	abstract fun draw()
	abstract fun calcOwnSize()
	abstract fun handleEvents()
	fun drawAndHandleEvents() {
		handleEvents()
		draw()
	}
}