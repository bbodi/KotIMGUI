package widget

import skin.Variant

class Button(widgetHandler: WidgetHandler, init: Button.() -> Unit) : Widget(widgetHandler) {
	var disabled: Boolean = false
	var label: String = ""
	var allow_multi_click = false
	var hover = false
		private set
	var down = false
	var variant = Variant.DEFAULT
	var onClick: (() -> Unit)? = null
	var margin = 5
	{
		init()
		calcOwnSize()
	}

	override fun draw() {
		widgetHandler.skin.drawButton(this)
	}

	override fun calcOwnSize() {
		widgetHandler.skin.calcButtonSize(this)
	}

	override fun handleEvents() {
		val was_hot = widgetHandler.hot_widget_id == label
		val was_active = widgetHandler.active_widget_id == label
		hover = widgetHandler.mouse_pos.is_in_rect(pos, AbsolutePos(width, height))
		down = was_active && !widgetHandler.leftMouseButton.just_released;

		if (widgetHandler.leftMouseButton.down && hover && !was_active) {
			widgetHandler.active_widget_id = label
		} else if (was_active && widgetHandler.leftMouseButton.just_released) {
			widgetHandler.active_widget_id = null
		}

		if (hover && !was_hot) {
			widgetHandler.hot_widget_id = label
		} else if (was_hot && !hover) {
			widgetHandler.hot_widget_id = null
		}
		val clicked = widgetHandler.leftMouseButton.just_released && hover
		if (clicked && onClick != null) {
			onClick!!()
		}
	}
}