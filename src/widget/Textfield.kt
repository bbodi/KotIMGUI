package widget

import skin.Variant
import timeline.StrValue
import timeline.widgetHandler

public class TextfieldData {
	var isCursorShown = false
	var cursorPos = 0
	var nextCursorToggleTick = 0
}

class Textfield(val text: StrValue, pos: Pos, init: Textfield.() -> Unit) : Widget(pos) {

	val isActive: Boolean
		get() = this.id == widgetHandler.active_widget_id

	var disabled: Boolean = false
	var variant = Variant.DEFAULT
	var margin = 5
	var defaultText = ""
	var hover = false
		private set
		get() {
			return widgetHandler.mousePos.is_in_rect(pos, AbsolutePos(width, height))
		}
	override var height = widgetHandler.skin.rowHeight
		private set

	val isCursorShown: Boolean
		get() = (widgetHandler.getWidgetData("Textfield", id) as TextfieldData).isCursorShown
	val cursorPos: Int
		get() = (widgetHandler.getWidgetData("Textfield", id) as TextfieldData).cursorPos

	{
		init()
	}
	override val id: Int = PositionBasedId(pos.x, pos.y, text.hashCode()).hashCode()

	override fun draw() {
		widgetHandler.skin.drawTextfield(this)
	}

	override fun handleEvents() {
		val was_hot = widgetHandler.hot_widget_id == id
		val was_active = widgetHandler.active_widget_id == id
		if (widgetHandler.leftMouseButton.down && hover && !was_active) {
			widgetHandler.active_widget_id = id
		}

		if (hover && !was_hot) {
			widgetHandler.hot_widget_id = id
		} else if (was_hot && !hover) {
			widgetHandler.hot_widget_id = null
		}

		val active = widgetHandler.active_widget_id == id
		val data = getOrCreateMyData()
		if (!active) {
			return
		}
		if (widgetHandler.pressedChar != null) {
			text.data = text.data + widgetHandler.pressedChar
			data.cursorPos++
		} else if (widgetHandler.backspace.down && data.cursorPos > 0) {
			val head = text.data.substring(0, data.cursorPos - 1)
			val tail = if (text.data.length > data.cursorPos) {
				text.data.substring(data.cursorPos)
			} else {
				""
			}
			text.data = head + tail
			data.cursorPos--
		}

		if (data.nextCursorToggleTick <= widgetHandler.currentTick) {
			data.isCursorShown = !data.isCursorShown
			data.nextCursorToggleTick = widgetHandler.currentTick + 700
		}
	}

	private fun getOrCreateMyData(): TextfieldData {
		val dataPtr = widgetHandler.getWidgetData("Textfield", id)
		return if (dataPtr == null) {
			val data = TextfieldData()
			widgetHandler.setWidgetData("Textfield", id, data)
			data
		} else {
			dataPtr as TextfieldData
		}
	}

	fun clicked(): Boolean {
		return (widgetHandler.leftMouseButton.just_released && hover)
	}
}