package widget

import timeline.AppSizeMetricData
import timeline.AppState
import kotlin.js.dom.html5.CanvasContext
import skin.Skin
import timeline.Ptr

open class TabPanel(val value: Ptr<Int>, pos: Pos, metrics: AppSizeMetricData, init: TabPanel.() -> Unit) : WidgetContainer(pos, metrics) {
	var visible: Ptr<Boolean> = Ptr(true);
	val items = arrayListOf<Button>()
	override val contentY: Int = this.pos.y + metrics.rowHeight + metrics.panelBorder
	{
		init()
		val (contentWidth, contentHeight) = calcContentSize()
		if (this.width == 0) {
			this.width = contentWidth + metrics.panelBorder*2
		}
		if (this.height == 0) {
			this.height = contentHeight + metrics.rowHeight + metrics.panelBorder*2
		}
		val headerRowWidth = items.foldRight(0, {(item, w) -> w+item.width})
		if (this.width < headerRowWidth) {
			this.width = headerRowWidth
		}
	}
	override val contentWidth: Int = width - metrics.panelBorder * 2
	override val contentHeight = height - metrics.rowHeight - (metrics.panelBorder*2)

	override fun draw(context: CanvasContext, skin: Skin) {
		if (!visible.value) {
			return
		}
		skin.drawTabPanel(this)
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
		items.withIndices().forEach {
			it.second.handleEvents(state)
			if (it.second.clicked) {
				value.value = it.first
			}
		}
		widgets.forEach { it.handleEvents(state) }
	}

	fun addTabPanelItem(label: String, buttonInit: Button.() -> Unit = {}) {
		val x = items.foldRight(pos.x, {(item, w) -> w+item.width})
		val pos = Pos(x, pos.y)
		items.add(Button(label, pos, metrics, buttonInit))
	}
}