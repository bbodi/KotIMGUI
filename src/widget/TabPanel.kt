package widget

import timeline.BooleanValue
import timeline.app
import timeline.IntValue
import timeline.context

open class TabPanel(val value: IntValue, pos: Pos, init: TabPanel.() -> Unit) : Panel(pos) {
	val items = arrayListOf<Button>()
	override val contentY: Int = this.pos.y + app.skin.rowHeight
	override val contentWidth: Int
		get() = width - marginY * 2
	override val contentHeight: Int
		get() = height - app.skin.rowHeight - marginY

	{
		init()
		val (w, h) = calcContentSize()
		if (this.width == 0) {
			this.width = w + marginY
		}
		if (this.height == 0) {
			this.height = h + app.skin.rowHeight + marginY
		}
		val headerRowWidth = items.foldRight(0, {(item, w) -> w+item.width})
		if (this.width < headerRowWidth) {
			this.width = headerRowWidth
		}
	}

	override fun draw() {
		if (!visible.value) {
			return
		}
		app.skin.drawTabPanel(this)
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
		items.withIndices().forEach {
			it.second.handleEvents()
			if (it.second.clicked) {
				value.value = it.first
			}
		}
		widgets.forEach { it.handleEvents() }
	}

	fun addTabPanelItem(label: String, buttonInit: Button.() -> Unit = {}) {
		val x = items.foldRight(pos.x, {(item, w) -> w+item.width})
		val pos = Pos(x, pos.y)
		items.add(Button(label, pos, buttonInit))
	}
}