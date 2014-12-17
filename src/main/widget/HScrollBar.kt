package widget

import timeline.atLeast
import timeline.atMost
import timeline.limit_into
import timeline.AppSizeMetricData
import timeline.AppState
import skin.Skin
import kotlin.js.dom.html5.CanvasContext
import timeline.Ptr


public class HScrollBar(val value: Ptr<Int>, pos: Pos, metrics: AppSizeMetricData, init: HScrollBar.() -> Unit = {}) : Widget(pos) {
	var disabled: Boolean = false
	var postfix: String = ""
	var hover = false
	var max_value = 100
	var min_value = 0

	{
		width = 100
		this.init()
	}

	override fun draw(context: CanvasContext, skin: Skin) {
		//skin.drawHorizontalScrollbar(app, this)
	}

	override fun handleEvents(state: AppState) {
		val h = 10
		val was_hot = state.hot_widget_id == value.hashCode()
		val was_active = state.active_widget_id == value.hashCode()
		val active = was_active && state.leftMouseButton.down
		hover = state.mousePos.isInRect(pos, Pos(width, h*2))

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
			val click_x = state.mousePos.x - this.pos.x
			val value_range = max_value - min_value
			val value_percent = (value.value - min_value) / value_range.toDouble()
			val orange_bar_w = width * value_percent
			if (clicked) {
				if (click_x < orange_bar_w) {
					value.value = (value.value -1) atLeast min_value
				} else if (click_x > orange_bar_w) {
					value.value = (value.value +1) atMost max_value
				}
			} else if (active) {
				val click_percent = click_x.toDouble() / width
				value.value = (min_value + value_range*click_percent).toInt().limit_into(min_value, max_value)
			}
		}
	}

}