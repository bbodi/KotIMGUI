package widget

import timeline.AppState
import timeline.AppSizeMetricData
import skin.Skin
import kotlin.js.dom.html5.CanvasContext


data class PositionBasedId(val x: Int, val y: Int, val id: Any) {}

abstract class Widget(val pos: Pos) {
	open var width: Int = 0
	open var height: Int = 0

	var parent: WidgetContainer? = null
	var additionalIdInfo: String = ""
	val id: Int
		get() = PositionBasedId(this.pos.x, this.pos.y, additionalIdInfo.hashCode()).hashCode()

	abstract fun draw(context: CanvasContext, skin: Skin)
	abstract fun handleEvents(state: AppState)
	fun drawAndHandleEvents(state: AppState, context: CanvasContext, skin: Skin) {
		handleEvents(state)
		draw(context, skin)
	}

	fun toRight(x: Int = 1): Pos {
		return Pos(this.pos.x + this.width + x, this.pos.y)
	}
}