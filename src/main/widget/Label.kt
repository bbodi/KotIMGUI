package widget

import skin.Variant
import timeline.AppSizeMetricData
import timeline.AppState
import skin.Skin
import kotlin.js.dom.html5.CanvasContext

class Label(val label: String, pos: Pos, metrics: AppSizeMetricData, init: Label.() -> Unit = {}) : Widget(pos) {
	override var height = metrics.rowHeight
		private set
	var variant = Variant.DEFAULT

	{
		init()
	}
	override var width = label.length * metrics.charWidth
		private set

	override fun draw(context: CanvasContext, skin: Skin) {
		skin.drawLabel(this)
	}

	override fun handleEvents(state: AppState) {

	}
}