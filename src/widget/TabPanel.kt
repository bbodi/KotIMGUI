package widget

import timeline.BooleanValue
import timeline.widgetHandler
import timeline.IntValue
import timeline.context

open class TabPanel(val value: IntValue, pos: Pos, init: TabPanel.() -> Unit) : Panel(pos) {
	val items = arrayListOf<Button>()
	override val id: Int = 0
	{
		init()
		val (w, h) = calcContentSize()
		if (this.width == 0) {
			this.width = w + margin
		}
		if (this.height == 0) {
			this.height = h + widgetHandler.skin.rowHeight + margin
		}
		val headerRowWidth = items.foldRight(0, {(item, w) -> w+item.width})
		if (this.width < headerRowWidth) {
			this.width = headerRowWidth
		}
	}

	override val contentX: Int
		get() = pos.x + margin
	override val contentY: Int
		get() = pos.y + widgetHandler.skin.rowHeight
	override val contentWidth: Int
		get() = width - margin * 2
	override val contentHeight: Int
		get() = height - widgetHandler.skin.rowHeight - margin

	override fun draw() {
		if (!visible.data) {
			return
		}
		widgetHandler.skin.drawTabPanel(this)
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
		items.withIndices().forEach {
			it.second.handleEvents()
			if (it.second.clicked) {
				value.data = it.first
			}
		}
		widgets.forEach { it.handleEvents() }
	}

	fun addTabPanelItem(label: String, buttonInit: Button.() -> Unit = {}) {
		val x = items.foldRight(pos.x, {(item, w) -> w+item.width})
		val pos = AbsolutePos(x, pos.y)
		items.add(Button(label, pos, buttonInit))
	}
}