package widget

import skin.Variant
import timeline.app
import timeline.BooleanValue
import timeline.IntValue

class RadioButton(val label: String, val value: IntValue, val order: Int, pos: Pos, init: RadioButton.() -> Unit = {}) : Widget(pos) {
	var disabled: Boolean = false
	var allow_multi_click = false
	var hover = false
		private set
		get() {
			return app.mousePos.is_in_rect(pos, Pos(width, height))
		}

	override var height = app.skin.rowHeight
		private set
	var variant = Variant.DEFAULT

	{
		init()
	}
	override var width = app.skin.charHeight/2 + app.skin.charWidth/2 + label.length * (app.skin.charWidth+1)
		private set

	override fun draw() {
		app.skin.drawRadioButton(this)
	}

	override fun handleEvents() {
		val was_hot = app.hot_widget_id == id

		if (hover && !was_hot) {
			app.hot_widget_id = id
		} else if (was_hot && !hover) {
			app.hot_widget_id = null
		}
		val clicked = app.leftMouseButton.just_released && hover
		if (clicked && !disabled) {
			value.value = order
		}
	}
}