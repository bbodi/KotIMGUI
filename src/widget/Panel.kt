package widget

class Panel(val widget_handler: WidgetHandler, init: WidgetContainer.() -> Unit) : WidgetContainer(widget_handler, init) {

	var margin: Int = 10

	{
		calcChildrenPos()
		calcOwnSize()
	}

	override fun draw() {
		widget_handler.skin.drawPanel(widget_handler, this)
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

	override fun calcOwnSize() {
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
		widgets.forEach { it.handleEvents() }
	}
}