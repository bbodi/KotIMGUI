package widget

import timeline.widgetHandler

abstract class WidgetContainer(pos: Pos) : Widget(pos) {
	val widgets = arrayListOf<Widget>();
	var margin: Int = 10

	fun Widget.plus() {
		addWidget(this)
	}

	fun addWidget(widget: Widget) {
		widget.parent = this
		widgets.add(widget)
	}


	fun calcOwnSize() {
		if (width != 0 || height != 0) {
			return
		}
		width = 0
		height = 0
		for (childWidget in widgets) {
			val childrenPos = childWidget.pos - pos
			if (childrenPos.x + childWidget.width > this.width) {
				this.width = childrenPos.x + childWidget.width
			}
			if (childrenPos.y + childWidget.height > this.height) {
				this.height = childrenPos.y + childWidget.height
			}
		}
		width += margin
		height += margin
	}

	fun WidgetContainer.downAlongLeftMargin(y: Int = 1): Pos {
		return RelativePos(pos.x + margin, fromLastWidgetBottom(y).y, array(Direction.DOWN, Direction.X_IS_ABSOLUTE))
	}
}