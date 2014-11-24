package widget

import timeline.IntValue
import timeline.at_least
import timeline.at_most
import timeline.limit_into


public class HScrollBar(widgetHandler: WidgetHandler, val value: IntValue, init: HScrollBar.() -> Unit = {}) : Widget(widgetHandler) {
	var disabled: Boolean = false
	var postfix: String = ""
	var hover = false
	var max_value = 100
	var min_value = 0

	{
		width = 100
		this.init()
		this.calcOwnSize()
	}

	override fun draw() {
		widgetHandler.skin.drawHorizontalScrollbar(widgetHandler, this)
	}

	override fun handleEvents() {
		val h = 10
		val was_hot = widgetHandler.hot_widget_id == value.hashCode()
		val was_active = widgetHandler.active_widget_id == value.hashCode()
		val active = was_active && widgetHandler.leftMouseButton.down
		hover = widgetHandler.mouse_pos.is_in_rect(pos, AbsolutePos(width, h*2))

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
			val click_x = widgetHandler.mouse_pos.x - this.pos.x
			val value_range = max_value - min_value
			val value_percent = (value.data - min_value) / value_range.toDouble()
			val orange_bar_w = width * value_percent
			if (clicked) {
				if (click_x < orange_bar_w) {
					value.data = (value.data-1) at_least min_value
				} else if (click_x > orange_bar_w) {
					value.data = (value.data+1) at_most max_value
				}
			} else if (active) {
				val click_percent = click_x.toDouble() / width
				value.data = (min_value + value_range*click_percent).toInt().limit_into(min_value, max_value)
			}
		}
	}

	override fun calcOwnSize() {

	}


	fun clicked(): Boolean {
		return (widgetHandler.leftMouseButton.just_released && hover)
	}
}