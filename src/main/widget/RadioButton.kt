package widget

import skin.Variant
import timeline.AppSizeMetricData
import timeline.AppState
import skin.Skin
import kotlin.js.dom.html5.CanvasContext
import timeline.Ptr

class RadioButton(val label: String, val value: Ptr<Int>, val order: Int, pos: Pos, metrics: AppSizeMetricData, init: RadioButton.() -> Unit = {}) : Widget(pos) {
	var disabled: Boolean = false
	var allow_multi_click = false
	var hover = false

	override var height = metrics.rowHeight
		private set
	var variant = Variant.DEFAULT

	{
		init()
	}
	override var width = metrics.charHeight/2 + metrics.charWidth/2 + label.length * (metrics.charWidth+1)
		private set

	override fun draw(context: CanvasContext, skin: Skin) {
		skin.drawRadioButton(this)
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
			value.value = order
		}
	}
}