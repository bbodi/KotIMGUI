package widget

import timeline.BooleanValue
import timeline.app
import skin.Variant
import timeline.context

open class Panel(pos: Pos, init: Panel.() -> Unit = {}) : WidgetContainer(pos) {
	var variant = Variant.DEFAULT
	var visible: BooleanValue = BooleanValue(true);
	{
		init()
		val (w, h) = calcContentSize()
		if (this.width == 0) {
			this.width = w + marginX
		}
		if (this.height == 0) {
			this.height = h + marginY
		}
	}
	var hover = false
		private set
		get() {
			return app.mousePos.is_in_rect(pos, Pos(width, height))
		}

	override fun draw() {
		if (!visible.value) {
			return
		}
		val x = pos.x
		val y = pos.y
		val w = width
		val h = height
		app.skin.drawPanelRect(x, y, w, h, variant)
		context.save()
		context.rect(contentX, contentY, contentWidth, contentHeight)
		context.clip()
		widgets.forEach { it.draw() }
		context.restore()
	}

	override fun handleEvents() {
		if (!visible.value) {
			return
		}
		widgets.forEach { it.handleEvents() }
	}
}