package widget

import skin.Variant
import timeline.BooleanValue
import timeline.AppSizeMetricData
import timeline.AppState
import skin.Skin

class Label(val label: String, pos: Pos, metrics: AppSizeMetricData, init: Label.() -> Unit = {}) : Widget(pos) {
	override var height = metrics.rowHeight
		private set
	var variant = Variant.DEFAULT

	{
		init()
	}
	override var width = label.length * metrics.charWidth
		private set

	override fun draw(skin: Skin) {
		skin.drawLabel(this)
	}

	override fun handleEvents(state: AppState) {

	}
}