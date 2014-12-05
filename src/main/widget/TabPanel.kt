package widget

import timeline.BooleanValue
import timeline.IntValue
import timeline.context
import timeline.AppSizeMetricData
import timeline.AppState
import skin.Skin

open class TabPanel(val value: IntValue, pos: Pos, val metrics: AppSizeMetricData, init: TabPanel.() -> Unit) : Panel(pos, metrics) {
	val items = arrayListOf<Button>()
	override val contentY: Int = this.pos.y + metrics.rowHeight
	override val contentWidth: Int
		get() = width - marginY * 2
	override val contentHeight = height - metrics.rowHeight - marginY

	{
		init()
		val (w, h) = calcContentSize()
		if (this.width == 0) {
			this.width = w + marginY
		}
		if (this.height == 0) {
			this.height = h + metrics.rowHeight + marginY
		}
		val headerRowWidth = items.foldRight(0, {(item, w) -> w+item.width})
		if (this.width < headerRowWidth) {
			this.width = headerRowWidth
		}
	}

	override fun draw(skin: Skin) {
		if (!visible.value) {
			return
		}
		skin.drawTabPanel(this)
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