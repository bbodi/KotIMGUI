package widget

import skin.Variant
import timeline.AppSizeMetricData
import timeline.AppState
import skin.Skin
import kotlin.js.dom.html5.CanvasContext
import timeline.Ptr

open class Panel(pos: Pos, metrics: AppSizeMetricData, init: Panel.() -> Unit = {}) : WidgetContainer(pos, metrics) {
	var variant = Variant.DEFAULT
	var visible: Ptr<Boolean> = Ptr(true);
	{
		init()
		val (w, h) = calcContentSize()
		if (this.width == 0) {
			this.width = w + metrics.panelBorder
		}
		if (this.height == 0) {
			this.height = h + metrics.panelBorder
		}
	}
	var hover = false

	override fun draw(context: CanvasContext, skin: Skin) {
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
		widgets.forEach { it.draw(context, skin) }
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