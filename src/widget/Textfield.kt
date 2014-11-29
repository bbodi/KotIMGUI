package widget

import skin.Variant
import timeline.StrValue
import timeline.widgetHandler
import timeline.at_most

public class TextfieldData {
	var isCursorShown = false
	var cursorPos = 0
	var nextCursorToggleTick = 0
}

open class Textfield(val text: StrValue, widthInCharacters: Int, pos: Pos, init: Textfield.() -> Unit) : Widget(pos) {

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
	override var width = widthInCharacters * widgetHandler.skin.charWidth
	override var height = widgetHandler.skin.rowHeight
		private set

	val isCursorShown: Boolean
		get() = (widgetHandler.getWidgetData("Textfield", id) as TextfieldData).isCursorShown
	val cursorPos: Int
		get() = (widgetHandler.getWidgetData("Textfield", id) as TextfieldData).cursorPos

	{
		init()
	}
	override val id: Int = PositionBasedId(this.pos.x, this.pos.y, text.hashCode()).hashCode()

	override fun draw() {
		widgetHandler.skin.drawTextfield(this)
	}

	override fun handleEvents() {
		val was_hot = widgetHandler.hot_widget_id == id
		val was_active = widgetHandler.active_widget_id == id
		val justClicked = !disabled && widgetHandler.leftMouseButton.down && hover && !was_active
		if (justClicked) {
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
		val clicked = widgetHandler.leftMouseButton.down && hover
		if (clicked) {
			data.cursorPos = (widgetHandler.mousePos.x - (this.pos.x + widgetHandler.skin.panelBorder)) / widgetHandler.skin.charWidth
			data.cursorPos = data.cursorPos.at_most(text.data.length)
		}
		handleInput(data, widgetHandler.pressedChar)

		if (data.nextCursorToggleTick <= widgetHandler.currentTick) {
			data.isCursorShown = !data.isCursorShown
			data.nextCursorToggleTick = widgetHandler.currentTick + 700
		}
	}

	private fun handleInput(data: TextfieldData, pressedChar: Char?) {
		if (widgetHandler.backspace.just_pressed && data.cursorPos > 0) {
			handleBackspace(data)
		} else if (widgetHandler.leftArrow.down && data.cursorPos > 0) {
			data.cursorPos--
		} else if (widgetHandler.rightArrow.down && data.cursorPos < text.data.length) {
			data.cursorPos++
		} else if (widgetHandler.home.down) {
			data.cursorPos = 0
		} else if (widgetHandler.end.down) {
			data.cursorPos = text.data.length
		} else if (pressedChar != null) {
			handlePressedChar(data, pressedChar)
		}
	}

	private fun handleBackspace(data: TextfieldData) {
		text.data = text.data.substring(0, data.cursorPos-1) + text.data.substring(data.cursorPos, text.data.length());
		data.cursorPos--
	}

	open protected fun handlePressedChar(data: TextfieldData, pressedChar: Char) {
		insertChar(data, pressedChar)
	}

	protected fun insertChar(data: TextfieldData, pressedChar: Char) {
		text.data = text.data.substring(0, data.cursorPos) + pressedChar + text.data.substring(data.cursorPos, text.data.length());
		data.cursorPos++
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
}