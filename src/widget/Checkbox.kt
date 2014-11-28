package widget

import skin.Variant
import timeline.widgetHandler
import timeline.BooleanValue

class Checkbox(val label: String, val value: BooleanValue, pos: Pos, init: Checkbox.() -> Unit = {}) : Widget(pos) {
	var disabled: Boolean = false
	var allow_multi_click = false
	var hover = false
		private set
		get() {
			return widgetHandler.mousePos.is_in_rect(pos, AbsolutePos(width, height))
		}

	override var height = widgetHandler.skin.rowHeight
		private set
	var variant = Variant.DEFAULT

	{
		init()
	}
	override var width = widgetHandler.skin.charHeight + widgetHandler.skin.charWidth/2 + label.length * widgetHandler.skin.charWidth
		private set
	override val id: Int = PositionBasedId(pos.x, pos.y, label.hashCode()).hashCode()

	override fun draw() {
		widgetHandler.skin.drawCheckbox(this)
	}

	override fun handleEvents() {
		val was_hot = widgetHandler.hot_widget_id == id

		if (hover && !was_hot) {
			widgetHandler.hot_widget_id = id
		} else if (was_hot && !hover) {
			widgetHandler.hot_widget_id = null
		}
		val clicked = widgetHandler.leftMouseButton.just_released && hover
		if (clicked && !disabled) {
			value.data = !value.data
		}
	}
}