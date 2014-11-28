package widget

import timeline.widgetHandler

abstract class WidgetContainer(pos: Pos) : Widget(pos) {
	abstract val contentX: Int
	abstract val contentY: Int
	abstract val contentHeight: Int
	abstract val contentWidth: Int

	val widgets = arrayListOf<Widget>();
	var margin: Int = 10

	fun Widget.plus() {
		addWidget(this)
	}

	fun addWidget(widget: Widget) {
		widget.parent = this
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

	fun WidgetContainer.downAlongLeftMargin(y: Int = 1): Pos {
		return RelativePos(pos.x + margin, fromLastWidgetBottom(y).y, array(Direction.DOWN, Direction.X_IS_ABSOLUTE))
	}
}