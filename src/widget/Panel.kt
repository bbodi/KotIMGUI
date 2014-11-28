package widget

import timeline.BooleanValue
import timeline.widgetHandler
import skin.Variant
import timeline.context

open class Panel(pos: Pos, init: Panel.() -> Unit = {}) : WidgetContainer(pos) {
	override val contentX: Int
		get() = pos.x + margin
	override val contentY: Int
		get() = pos.y + margin
	override val contentWidth: Int
		get() = width - margin * 2
	override val contentHeight: Int
		get() = height - margin * 2
	var variant = Variant.DEFAULT
	override val id: Int = 0
	var visible: BooleanValue = BooleanValue(true);
	{
		init()
		val (w, h) = calcContentSize()
		if (this.width == 0) {
			this.width = w + margin
		}
		if (this.height == 0) {
			this.height = h + margin
		}
	}
	var hover = false
		private set
		get() {
			return widgetHandler.mousePos.is_in_rect(pos, AbsolutePos(width, height))
		}

	override fun draw() {
		if (!visible.data) {
			return
		}
		val x = pos.x
		val y = pos.y
		val w = width
		val h = height
		widgetHandler.skin.drawPanelRect(x, y, w, h, variant)
		context.save()
		context.rect(contentX, contentY, contentWidth, contentHeight)
		context.clip()
		widgets.forEach { it.draw() }
		context.restore()
	}

	override fun handleEvents() {
		if (!visible.data) {
			return
		}
		widgets.forEach { it.handleEvents() }
	}
}