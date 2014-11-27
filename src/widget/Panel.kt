package widget

import timeline.BooleanValue
import timeline.widgetHandler

open class Panel(pos: Pos, init: Panel.() -> Unit) : WidgetContainer(pos) {
	override val id: Int = 0
	var visible: BooleanValue = BooleanValue(true)
	var onClickOut: (() -> Unit)? = null

	{
		init()
		calcOwnSize()
	}
	var hover = false
		private set
		get() {
			return widgetHandler.mousePos.is_in_rect(pos, AbsolutePos(width, height))
		}

	override fun draw() {
		if (!visible.data) {
			return
		}
		widgetHandler.skin.drawPanel(this)
		widgets.forEach { it.draw() }
	}

	override fun handleEvents() {
		if (!visible.data) {
			return
		}
		widgets.forEach { it.handleEvents() }
		val clickedOut = !hover && widgetHandler.leftMouseButton.just_released
		if (clickedOut && onClickOut != null) {
			onClickOut!!()
		}
	}
}