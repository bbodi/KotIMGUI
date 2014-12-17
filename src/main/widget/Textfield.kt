package widget

import skin.Variant
import timeline.atMost
import timeline.InputButton
import timeline.AppSizeMetricData
import timeline.AppState
import timeline.AppSizeMetricData
import skin.Skin
import timeline.Keys
import kotlin.js.dom.html5.CanvasContext
import timeline.debugLines
import timeline.Ptr

open public class TextfieldData {
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

open class Textfield(val textPtr: Ptr<String>, widthInCharacters: Int, pos: Pos, metrics: AppSizeMetricData, init: Textfield.() -> Unit = {}) : Widget(pos) {

	var disabled: Boolean = false
	var variant = Variant.DEFAULT
	var margin = 5
	var defaultText = ""
	var onChange: (()->Unit)? = null
	var isActive = false


	override var width = widthInCharacters * metrics.charWidth
	override var height = metrics.rowHeight
		private set

	var cursorPos: Int = textPtr.value.length
	{
		init()
	}

	var isCursorShown: Boolean = false
	var hover = false

	open protected fun isHover(state: AppState): Boolean {
		return state.mousePos.isInRect(pos, Pos(width, height))
	}

	open protected fun isActive(state: AppState): Boolean {
		return state.isActive(this)
	}

	override fun draw(context: CanvasContext, skin: Skin) {
		skin.drawTextfield(this)
	}

	override fun handleEvents(state: AppState) {
		readPersistentInfo(state)
		hover = isHover(state)
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

		isActive = isActive(state)
		if (!isActive) {
			return
		}
		val clicked = state.leftMouseButton.down && hover
		if (clicked) {
			cursorPos = (state.mousePos.x - (this.pos.x + state.metrics.panelBorder)) / state.metrics.charWidth
			cursorPos = cursorPos.atMost(textPtr.value.length)
		}

		val beforeText = textPtr.value
		handleInput(state.pressedChar, state)
		val afterText = textPtr.value
		if (beforeText != afterText) {
			onTextChanged()
			textPtr.value = getShownText()
			onChange?.invoke()
		}

		val (isNew, data) = getOrCreateMyData(state)
		if (data.nextCursorToggleTick <= state.currentTick) {
			data.isCursorShown = !data.isCursorShown
			data.nextCursorToggleTick = state.currentTick + 700
		}
		data.cursorPos = cursorPos
	}

	private fun readPersistentInfo(state: AppState) {
		val (isNew, data) = getOrCreateMyData(state)
		if (!isNew) {
			cursorPos = data.cursorPos
		}
		isCursorShown = data.isCursorShown
	}

	open protected fun handleInput(pressedChar: Char?, state: AppState) {
		if (state.isKeyDown(Keys.Del) && cursorPos < textPtr.value.length && state.isPressable(Keys.Del)) {
			handleDelButton(state.isKeyDown(Keys.Ctrl))
			state.clearKeysExcept(Keys.Del)
			state.setPressed(Keys.Del)
		} else if (state.isKeyDown(Keys.Backspace) && cursorPos > 0 && state.isPressable(Keys.Backspace)) {
			handleBackspace(state.isKeyDown(Keys.Ctrl))
			state.clearKeysExcept(Keys.Backspace)
			state.setPressed(Keys.Backspace)
		} else if (state.isKeyDown(Keys.LeftArrow) && cursorPos > 0 && state.isPressable(Keys.LeftArrow)) {
			cursorPos--
			state.clearKeysExcept(Keys.LeftArrow)
			state.setPressed(Keys.LeftArrow)
		} else if (state.isKeyDown(Keys.RightArrow) && cursorPos < textPtr.value.length && state.isPressable(Keys.RightArrow)) {
			cursorPos++
			state.clearKeysExcept(Keys.RightArrow)
			state.setPressed(Keys.RightArrow)
		} else if (state.isKeyDown(Keys.Home)) {
			state.clearKeysExcept(null)
			cursorPos = 0
		} else if (state.isKeyDown(Keys.End)) {
			state.clearKeysExcept(null)
			cursorPos = textPtr.value.length
		} else if (pressedChar != null) {
			state.clearKeysExcept(null)
			handlePressedChar(pressedChar)
		}
	}

	open protected fun onTextChanged() {
	}

	private fun handleBackspace(isCtrlDown: Boolean) {
		if (isCtrlDown) {
			textPtr.value = ""
			cursorPos = 0
		} else {
			textPtr.value = textPtr.value.substring(0, cursorPos - 1) + textPtr.value.substring(cursorPos, textPtr.value.length());
			cursorPos--
		}
	}

	private fun handleDelButton(isCtrlDown: Boolean) {
		if (isCtrlDown) {
			textPtr.value = textPtr.value.substring(0, cursorPos)
		} else {
			textPtr.value = textPtr.value.substring(0, cursorPos) + textPtr.value.substring(cursorPos+1, textPtr.value.length());
		}
	}

	open protected fun handlePressedChar(pressedChar: Char) {
		insertChar(pressedChar)
	}

	protected fun insertChar(pressedChar: Char) {
		textPtr.value = textPtr.value.substring(0, cursorPos) + pressedChar + textPtr.value.substring(cursorPos, textPtr.value.length());
		cursorPos++
	}

	open protected fun getOrCreateMyData(state: AppState): Pair<Boolean, TextfieldData> {
		val dataPtr = state.getWidgetData("Textfield", id)
		return if (dataPtr == null) {
			val data = TextfieldData()
			state.setWidgetData("Textfield", id, data)
			Pair(true, data)
		} else {
			Pair(false, dataPtr as TextfieldData)
		}
	}

	open protected fun getShownText(): String {
		return textPtr.value
	}
}