package widget

import timeline.BooleanValue

open class Panel(val widget_handler: WidgetHandler, init: Panel.() -> Unit) : WidgetContainer(widget_handler) {
	override val id: Int = 0
	var visible: BooleanValue = BooleanValue(true)
	var margin: Int = 10

	{
		init()
		calcChildrenPos()
		calcOwnSize()
	}
	var hover = false
		private set
		get() {
			return widgetHandler.mouse_pos.is_in_rect(pos, AbsolutePos(width, height))
		}

	override fun draw() {
		if (!visible.data) {
			return
		}
		widget_handler.skin.drawPanel(this)
		widgets.forEach { it.draw() }
	}

	fun calcChildrenPos() {
		var lastDrawnWidget: Widget? = null
		val myPos = (this.pos as AbsolutePos)
		for (w in widgets) {
			val widgetPos = w.pos

			w.pos = when (widgetPos) {
				is AbsolutePos -> myPos + widgetPos + AbsolutePos(margin, margin)
				is RelativePos -> {
					if (lastDrawnWidget == null) {
						myPos + AbsolutePos(widgetPos.x, widgetPos.y)
					} else {
						widgetPos.calcAbsolutePosFrom(lastDrawnWidget!!.pos as AbsolutePos, w.width, w.height)
					}
				}
				else -> throw IllegalArgumentException();
			}
			lastDrawnWidget = w
		}
	}

	fun calcOwnSize() {
		if (width != 0 || height != 0) {
			return
		}
		width = 0
		height = 0
		for (childWidget in widgets) {
			if (childWidget.pos.x - this.pos.x + childWidget.width > this.width) {
				this.width = childWidget.pos.x - this.pos.x + childWidget.width
			}
			if (childWidget.pos.y - this.pos.y + childWidget.height > this.height) {
				this.height = childWidget.pos.y - this.pos.y + childWidget.height
			}
		}
		width += margin
		height += margin
	}

	override fun handleEvents() {
		if (!visible.data) {
			return
		}
		widgets.forEach { it.handleEvents() }
		val clickedOut = !hover && widgetHandler.leftMouseButton.just_released
		if (clickedOut) {
			visible.data = false
		}
	}
}