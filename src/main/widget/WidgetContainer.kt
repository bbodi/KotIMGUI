package widget

import timeline.AppSizeMetricData

abstract class WidgetContainer(pos: Pos, val metrics: AppSizeMetricData) : Widget(pos) {
	val widgets = arrayListOf<Widget>();
	open val contentX = this.pos.x + metrics.panelBorder
	open val contentY = this.pos.y + metrics.panelBorder
	open val contentWidth: Int
		get() = width - metrics.panelBorder * 2
	open val contentHeight: Int
		get() = height - metrics.panelBorder * 2

	fun Widget.plus() {
		addWidget(this)
	}

	fun addWidget(widget: Widget) {
		widget.parent = this
		widget.additionalIdInfo += this.additionalIdInfo
		widgets.add(widget)
	}


	fun calcContentSize(): Pair<Int, Int> {
		var calculatedWidth = 0
		var calculatedHeight = 0
		for (childWidget in widgets) {
			val childrenPos = childWidget.pos - Pos(contentX, contentY)
			if (this.width == 0) {
				if (childrenPos.x + childWidget.width > calculatedWidth) {
					calculatedWidth = childrenPos.x + childWidget.width
				}
			}
			if (this.height == 0) {
				if (childrenPos.y + childWidget.height > calculatedHeight) {
					calculatedHeight = childrenPos.y + childWidget.height
				}
			}
		}
		return Pair(calculatedWidth, calculatedHeight)
	}


	fun downAlongLeftMargin(y: Int = 0): Pos {
		val fromY = if (widgets.last != null) {
			widgets.last!!.pos.y + widgets.last!!.height
		} else contentY
		return Pos(contentX, fromY + y)
	}

	fun toRightFromLastWidget(x: Int = 1): Pos {
		val lastWidget = widgets.last!!
		return Pos(lastWidget.pos.x + lastWidget.width + x, lastWidget.pos.y)
	}

	fun plus(func: WidgetContainer.() -> Unit) {
		func()
	}
}