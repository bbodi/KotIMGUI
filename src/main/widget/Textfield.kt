package widget

import skin.Variant
import timeline.StrValue
import timeline.at_most
import timeline.InputButton
import timeline.AppSizeMetricData
import timeline.AppState
import timeline.AppSizeMetricData
import skin.Skin

public class TextfieldData {
	var isCursorShown = false
	var cursorPos = 0
	var nextCursorToggleTick = 0
}

public class ButtonTimer {
	private var lastPressed = 0
	private var pressCount = 0

	fun isPressable(now: Int): Boolean = (now - lastPressed) > (200 + (if (pressCount==1) 1 else 0)*800)
	fun setPressed(now: Int) {
		pressCount++
		lastPressed = now
	}

	fun clear() {
		pressCount = 0
		lastPressed = 0
	}
}

open class Textfield(val text: StrValue, widthInCharacters: Int, pos: Pos, metrics: AppSizeMetricData, init: Textfield.() -> Unit) : Widget(pos) {

	var disabled: Boolean = false
	var variant = Variant.DEFAULT
	var margin = 5
	var defaultText = ""
	var onChange: (()->Unit)? = null
	var isActive = false


	override var width = widthInCharacters * metrics.charWidth
	override var height = metrics.rowHeight
		private set

	{
		additionalIdInfo = text.hashCode().toString()
		init()
	}

	var isCursorShown: Boolean = false
	var cursorPos: Int = 0
	var hover = false

	override fun draw(skin: Skin) {
		skin.drawTextfield(this)
	}

	override fun handleEvents(state: AppState) {
		val data = getOrCreateMyData(state)
		isCursorShown = data.isCursorShown
		cursorPos = data.cursorPos
		hover = state.mousePos.isInRect(pos, Pos(width, height))
		val was_hot = state.hot_widget_id == id
		val was_active = state.active_widget_id == id
		val justClicked = !disabled && state.leftMouseButton.down && hover && !was_active
		if (justClicked) {
			state.active_widget_id = id
		}

		if (hover && !was_hot) {
			state.hot_widget_id = id
		} else if (was_hot && !hover) {
			state.hot_widget_id = null
		}

		isActive = state.isActive(this)
		if (!isActive) {
			return
		}
		val clicked = state.leftMouseButton.down && hover
		if (clicked) {
			data.cursorPos = (state.mousePos.x - (this.pos.x + state.metrics.panelBorder)) / state.metrics.charWidth
			data.cursorPos = data.cursorPos.at_most(text.value.length)
		}
		handleInput(data, state.pressedChar, state)

		if (data.nextCursorToggleTick <= state.currentTick) {
			data.isCursorShown = !data.isCursorShown
			data.nextCursorToggleTick = state.currentTick + 700
		}
	}

	private fun handleInput(data: TextfieldData, pressedChar: Char?, state: AppState) {
		val beforeValue = text.value
		if (state.backspace.down && data.cursorPos > 0 && state.isPressable(state.backspace)) {
			handleBackspace(data)
			state.clearButtonsExcept(state.backspace)
			state.setPressed(state.backspace)
		} else if (state.leftArrow.down && data.cursorPos > 0 && state.isPressable(state.leftArrow)) {
			data.cursorPos--
			state.clearButtonsExcept(state.leftArrow)
			state.setPressed(state.leftArrow)
		} else if (state.rightArrow.down && data.cursorPos < text.value.length && state.isPressable(state.rightArrow)) {
			data.cursorPos++
			state.clearButtonsExcept(state.rightArrow)
			state.setPressed(state.rightArrow)
		} else if (state.home.down) {
			state.clearButtonsExcept(null)
			data.cursorPos = 0
		} else if (state.end.down) {
			state.clearButtonsExcept(null)
			data.cursorPos = text.value.length
		} else if (pressedChar != null) {
			state.clearButtonsExcept(null)
			handlePressedChar(data, pressedChar)
		}
		val afterValue = text.value
		if (beforeValue != afterValue && onChange != null) {
			onChange!!()
		}
	}

	private fun handleBackspace(data: TextfieldData) {
		text.value = text.value.substring(0, data.cursorPos-1) + text.value.substring(data.cursorPos, text.value.length());
		data.cursorPos--
	}

	open protected fun handlePressedChar(data: TextfieldData, pressedChar: Char) {
		insertChar(data, pressedChar)
	}

	protected fun insertChar(data: TextfieldData, pressedChar: Char) {
		text.value = text.value.substring(0, data.cursorPos) + pressedChar + text.value.substring(data.cursorPos, text.value.length());
		data.cursorPos++
	}

	protected fun getOrCreateMyData(state: AppState): TextfieldData {
		val dataPtr = state.getWidgetData("Textfield", id)
		return if (dataPtr == null) {
			val data = TextfieldData()
			state.setWidgetData("Textfield", id, data)
			data
		} else {
			dataPtr as TextfieldData
		}
	}
}