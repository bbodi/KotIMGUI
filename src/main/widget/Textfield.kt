package widget

import skin.Variant
import timeline.StrValue
import timeline.at_most
import timeline.InputButton
import timeline.AppSizeMetricData
import timeline.AppState
import timeline.AppSizeMetricData
import skin.Skin
import timeline.Keys

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

open class Textfield(val text: StrValue, widthInCharacters: Int, pos: Pos, metrics: AppSizeMetricData, init: Textfield.() -> Unit = {}) : Widget(pos) {

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
	var cursorPos: Int = text.value.length
	var hover = false

	override fun draw(skin: Skin) {
		skin.drawTextfield(this)
	}

	override fun handleEvents(state: AppState) {
		val (isNew, data) = getOrCreateMyData(state)
		if (!isNew) {
			cursorPos = data.cursorPos
		}
		isCursorShown = data.isCursorShown
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
		handleInput(state.pressedChar, state)

		if (data.nextCursorToggleTick <= state.currentTick) {
			data.isCursorShown = !data.isCursorShown
			data.nextCursorToggleTick = state.currentTick + 700
		}
		data.cursorPos = cursorPos
	}

	private fun handleInput(pressedChar: Char?, state: AppState) {
		val beforeValue = text.value
		val fakk = state.isPressable(Keys.Backspace)
		val makk = state.isKeyDown(Keys.Backspace)
		val sakk = cursorPos
		if (makk && cursorPos > 0 && fakk) {
			handleBackspace(state.isKeyDown(Keys.Ctrl))
			state.clearKeysExcept(Keys.Backspace)
			state.setPressed(Keys.Backspace)
		} else if (state.isKeyDown(Keys.LeftArrow) && cursorPos > 0 && state.isPressable(Keys.LeftArrow)) {
			cursorPos--
			state.clearKeysExcept(Keys.LeftArrow)
			state.setPressed(Keys.LeftArrow)
		} else if (state.isKeyDown(Keys.RightArrow) && cursorPos < text.value.length && state.isPressable(Keys.RightArrow)) {
			cursorPos++
			state.clearKeysExcept(Keys.RightArrow)
			state.setPressed(Keys.RightArrow)
		} else if (state.isKeyDown(Keys.Home)) {
			state.clearKeysExcept(null)
			cursorPos = 0
		} else if (state.isKeyDown(Keys.End)) {
			state.clearKeysExcept(null)
			cursorPos = text.value.length
		} else if (pressedChar != null) {
			state.clearKeysExcept(null)
			handlePressedChar(pressedChar)
		}
		val afterValue = text.value
		if (beforeValue != afterValue && onChange != null) {
			onChange!!()
		}
	}

	private fun handleBackspace(isCtrlDown: Boolean) {
		if (isCtrlDown) {
			text.value = ""
			cursorPos = 0
		} else {
			text.value = text.value.substring(0, cursorPos - 1) + text.value.substring(cursorPos, text.value.length());
			cursorPos--
		}
	}

	open protected fun handlePressedChar(pressedChar: Char) {
		insertChar(pressedChar)
	}

	protected fun insertChar(pressedChar: Char) {
		text.value = text.value.substring(0, cursorPos) + pressedChar + text.value.substring(cursorPos, text.value.length());
		cursorPos++
	}

	protected fun getOrCreateMyData(state: AppState): Pair<Boolean, TextfieldData> {
		val dataPtr = state.getWidgetData("Textfield", id)
		return if (dataPtr == null) {
			val data = TextfieldData()
			state.setWidgetData("Textfield", id, data)
			Pair(true, data)
		} else {
			Pair(false, dataPtr as TextfieldData)
		}
	}
}