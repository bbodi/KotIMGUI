package widget

import timeline.BooleanValue
import skin.Variant
import timeline.context
import timeline.AppSizeMetricData
import timeline.AppState
import skin.Skin

open class Panel(pos: Pos, metrics: AppSizeMetricData, init: Panel.() -> Unit = {}) : WidgetContainer(pos, metrics) {
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

	override fun draw(skin: Skin) {
		if (!visible.value) {
			return
		}
		val x = pos.x
		val y = pos.y
		val w = width
		val h = height
		skin.drawPanelRect(x, y, w, h, variant)
		context.save()
		context.rect(contentX, contentY, contentWidth, contentHeight)
		context.clip()
		widgets.forEach { it.draw(skin) }
		context.restore()
	}

	override fun handleEvents(state: AppState) {
		if (!visible.value) {
			return
		}
		hover = state.mousePos.isInRect(pos, Pos(width, height))
		widgets.forEach { it.handleEvents(state) }
	}
}