package widget

import timeline.AppSizeMetricData

abstract class WidgetContainer(pos: Pos, metrics: AppSizeMetricData) : Widget(pos) {
	val widgets = arrayListOf<Widget>();
	val marginX = metrics.panelBorder
	val marginY = metrics.panelBorder
	open val contentX = this.pos.x + marginX
	open val contentY = this.pos.y + marginY
	open val contentWidth: Int
		get() = width - marginX * 2
	open val contentHeight: Int
		get() = height - marginY * 2

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
			val childrenPos = childWidget.pos - pos
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


	fun downAlongLeftMargin(y: Int = 1): Pos {
		val fromY = if (widgets.last != null) {
			widgets.last!!.pos.y + widgets.last!!.height
		} else contentY
		return Pos(contentX, fromY + y)
	}

	fun downUnderMargin(): Pos {
		return downAlongLeftMargin(marginX)
	}

	fun toRightFromLastWidget(x: Int = 1): Pos {
		val lastWidget = widgets.last!!
		return Pos(lastWidget.pos.x + lastWidget.width + x, lastWidget.pos.y)
	}
}