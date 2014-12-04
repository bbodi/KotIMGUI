package widget

import skin.Variant
import timeline.app
import timeline.BooleanValue

class Label(val label: String, pos: Pos, init: Label.() -> Unit = {}) : Widget(pos) {
	override var height = app.skin.rowHeight
		private set
	var variant = Variant.DEFAULT

	{
		init()
	}
	override var width = label.length * app.skin.charWidth
		private set

	override fun draw() {
		app.skin.drawLabel(this)
	}

	override fun handleEvents() {

	}
}