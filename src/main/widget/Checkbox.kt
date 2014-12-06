package widget

import skin.Variant
import timeline.BooleanValue
import timeline.AppSizeMetricData
import timeline.AppState
import skin.Skin

class Checkbox(val label: String, val value: BooleanValue, pos: Pos, metrics: AppSizeMetricData, init: Checkbox.() -> Unit = {}) : Widget(pos) {
	var disabled: Boolean = false
	var allow_multi_click = false
	var hover = false

	override var height = metrics.rowHeight
		private set
	var variant = Variant.DEFAULT

	{
		init()
	}
	override var width = metrics.charHeight + metrics.charWidth/2 + label.length * metrics.charWidth
		private set

	override fun draw(skin: Skin) {
		skin.drawCheckbox(this)
	}

	override fun handleEvents(state: AppState) {
		hover = state.mousePos.isInRect(pos, Pos(width, height))
		val was_hot = state.hot_widget_id == id

		if (hover && !was_hot) {
			state.hot_widget_id = id
		} else if (was_hot && !hover) {
			state.hot_widget_id = null
		}
		val clicked = state.leftMouseButton.just_released && hover
		if (clicked && !disabled) {
			value.value = !value.value
		}
	}
}