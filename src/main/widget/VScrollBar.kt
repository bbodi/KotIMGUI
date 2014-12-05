package widget

import timeline.IntValue
import timeline.at_least
import timeline.at_most
import timeline.limit_into
import timeline.AppSizeMetricData
import timeline.AppState
import skin.Skin


public class VScrollBar(val value: IntValue, pos: Pos, metrics: AppSizeMetricData, init: VScrollBar.() -> Unit = {}) : Widget(pos) {
	var disabled: Boolean = false
	var postfix: String = ""
	var hover = false
	var max_value = 100
	var min_value = 0

	{
		height = 100
		this.init()
	}

	override fun draw(skin: Skin) {
		//metrics.drawVerticalScrollbar(app, this)
	}


	override fun handleEvents(state: AppState) {
		val w = state.metrics.charWidth
		val was_hot = state.hot_widget_id == value.hashCode()
		val was_active = state.active_widget_id == value.hashCode()
		val active = was_active && state.leftMouseButton.down
		hover = state.mousePos.isInRect(pos, Pos(w*2, height))

		if (state.leftMouseButton.down && hover && !was_active) {
			state.active_widget_id = value.hashCode()
		} else if (was_active && state.leftMouseButton.just_released) {
			state.active_widget_id = null
		}

		if (hover && !was_hot) {
			state.hot_widget_id = value.hashCode()
		} else if (was_hot && !hover) {
			state.hot_widget_id = null
		}

		val clicked = hover && state.leftMouseButton.just_released
		if (clicked || active) {
			val click_y = state.mousePos.y - this.pos.y
			val value_range = max_value - min_value
			val value_percent = (value.value - min_value) / value_range.toDouble()
			val orange_bar_h = height * value_percent
			if (clicked) {
				if (click_y < orange_bar_h) {
					value.value = (value.value - 1) at_least min_value
				} else if (click_y > orange_bar_h) {
					value.value = (value.value + 1) at_most max_value
				}
			} else if (active) {
				val click_percent = click_y.toDouble() / height
				value.value = (min_value + value_range * click_percent).toInt().limit_into(min_value, max_value)
			}
		}
	}

}