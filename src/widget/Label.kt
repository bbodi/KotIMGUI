package widget

import skin.Variant
import timeline.widgetHandler
import timeline.BooleanValue

class Label(val label: String, pos: Pos, init: Label.() -> Unit = {}) : Widget(pos) {
	override var height = widgetHandler.skin.rowHeight
		private set
	var variant = Variant.DEFAULT

	{
		init()
	}
	override var width = label.length * widgetHandler.skin.charWidth
		private set
	override val id: Int = PositionBasedId(pos.x, pos.y, label.hashCode()).hashCode()

	override fun draw() {
		widgetHandler.skin.drawLabel(this)
	}

	override fun handleEvents() {

	}
}