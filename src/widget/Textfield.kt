package widget

import skin.Variant
import timeline.StrValue

public class TextfieldData {
	var isCursorShown = false
	var cursorPos = 0
	var nextCursorToggleTick = 0
}

private data class Id(val x: Int, val y: Int, val text: StrValue)

class Textfield(val text: StrValue, widgetHandler: WidgetHandler, init: Textfield.() -> Unit) : Widget(widgetHandler) {

	val isActive: Boolean
		get() = this.id == widgetHandler.active_widget_id

	var id: Int  = 0
		private set
	var disabled: Boolean = false
	var hover = false
	var variant = Variant.DEFAULT
	var margin = 5
	var defaultText = ""

	val isCursorShown: Boolean
		get() = (widgetHandler.widgetDatas[id] as TextfieldData).isCursorShown
	val cursorPos: Int
		get() = (widgetHandler.widgetDatas[id] as TextfieldData).cursorPos

	{
		init()
		calcOwnSize()
	}

	override fun draw() {
		widgetHandler.skin.drawTextfield(this)
	}

	override fun calcOwnSize() {
		widgetHandler.skin.calcTextFieldSize(this)

	}

	override fun handleEvents() {
		id = Id(pos.x, pos.y, text).hashCode()
		val was_hot = widgetHandler.hot_widget_id == id
		val was_active = widgetHandler.active_widget_id == id
		hover = widgetHandler.mouse_pos.is_in_rect(pos, AbsolutePos(width, height))
		if (widgetHandler.leftMouseButton.down && hover && !was_active) {
			widgetHandler.active_widget_id = id
		}

		if (hover && !was_hot) {
			widgetHandler.hot_widget_id = id
		} else if (was_hot && !hover) {
			widgetHandler.hot_widget_id = null
		}

		val active = widgetHandler.active_widget_id == id
		val data = getOrCreateMyData(id)
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

	private fun getOrCreateMyData(id: Int): TextfieldData {
		val dataPtr = widgetHandler.widgetDatas[id]
		return if (dataPtr == null) {
			val data = TextfieldData()
			widgetHandler.widgetDatas.put(id, data)
			data
		} else {
			dataPtr as TextfieldData
		}
	}

	fun clicked(): Boolean {
		return (widgetHandler.leftMouseButton.just_released && hover)
	}
}