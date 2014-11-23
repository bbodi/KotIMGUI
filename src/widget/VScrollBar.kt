package widget

import timeline.IntValue
import timeline.at_least
import timeline.at_most
import timeline.limit_into


public class VScrollBar(widgetHandler: WidgetHandler, val value: IntValue, init: VScrollBar.() -> Unit = {}) : Widget(widgetHandler) {
	var disabled: Boolean = false
	var postfix: String = ""
	var hover = false
	var max_value = 100
	var min_value = 0

	{
		height = 100
		this.init()
		calcOwnSize()
	}

	override fun draw() {
		widgetHandler.skin.drawVerticalScrollbar(widgetHandler, this)
	}

	override fun calcOwnSize() {

	}

	override fun handleEvents() {
		val w = widgetHandler.char_w()
		val was_hot = widgetHandler.hot_widget_id == value.hashCode()
		val was_active = widgetHandler.active_widget_id == value.hashCode()
		val active = was_active && widgetHandler.leftMouseButton.down
		hover = widgetHandler.mouse_pos.is_in_rect(pos, AbsolutePos(w*2, height))

		if (widgetHandler.leftMouseButton.down && hover && !was_active) {
			widgetHandler.active_widget_id = value.hashCode()
		} else if (was_active && widgetHandler.leftMouseButton.just_released) {
			widgetHandler.active_widget_id = null
		}

		if (hover && !was_hot) {
			widgetHandler.hot_widget_id = value.hashCode()
		} else if (was_hot && !hover) {
			widgetHandler.hot_widget_id = null
		}

		val clicked = hover && widgetHandler.leftMouseButton.just_released
		if (clicked || active) {
			val click_y = widgetHandler.mouse_pos.y - this.pos.y
			val value_range = max_value - min_value
			val value_percent = (value.data - min_value) / value_range.toDouble()
			val orange_bar_h = height * value_percent
			if (clicked) {
				if (click_y < orange_bar_h) {
					value.data = (value.data - 1) at_least min_value
				} else if (click_y > orange_bar_h) {
					value.data = (value.data + 1) at_most max_value
				}
			} else if (active) {
				val click_percent = click_y.toDouble() / height
				value.data = (min_value + value_range * click_percent).toInt().limit_into(min_value, max_value)
			}
		}
	}


	fun clicked(): Boolean {
		return (widgetHandler.leftMouseButton.just_released && hover)
	}
}