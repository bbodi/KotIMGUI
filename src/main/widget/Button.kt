package widget

import timeline.AppSizeMetricData
import timeline.AppState
import skin.Variant
import skin.Skin

class Button(val label: String, pos: Pos, val metrics: AppSizeMetricData, init: Button.() -> Unit) : Widget(pos) {

	var clicked: Boolean = false
	var disabled: Boolean = false
	var hover = false



	override var height = metrics.rowHeight
	var down = false
	var variant = Variant.DEFAULT
	var onClick: (() -> Unit)? = null
	{
		init()
	}

	override var width = 0
		get() = if ($width == 0) (label.length+2) * metrics.charWidth else $width

	override fun draw(skin: Skin) {
		skin.drawButton(this)
	}

	override fun handleEvents(state: AppState) {
		hover = state.mousePos.isInRect(pos, Pos(width, height))
		val was_hot = state.hot_widget_id == id
		val was_active = state.active_widget_id == id

		if (state.leftMouseButton.down && hover && !was_active) {
			state.active_widget_id = id
		} else if (was_active && state.leftMouseButton.just_released) {
			state.active_widget_id = null
		}
		val isActive = state.leftMouseButton.down && hover
		down = isActive

		if (hover && !was_hot) {
			state.hot_widget_id = id
		} else if (was_hot && !hover) {
			state.hot_widget_id = null
		}
		clicked = !disabled && state.leftMouseButton.just_released && hover && was_active
		if (clicked && onClick != null) {
			onClick!!()
		}
	}
}