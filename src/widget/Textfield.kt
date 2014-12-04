package widget

import skin.Variant
import timeline.StrValue
import timeline.app
import timeline.at_most
import timeline.InputButton

public class TextfieldData {
	var isCursorShown = false
	var cursorPos = 0
	var nextCursorToggleTick = 0
	private val buttonTimers = hashMapOf(app.leftArrow to ButtonTimer(), app.rightArrow to ButtonTimer(), app.backspace to ButtonTimer())

	fun isPressable(btn: InputButton, now: Int) = this.buttonTimers[btn]!!.isPressable(now)
	fun clearButtonsExcept(btn: InputButton?) {
		buttonTimers.filter { it.key != btn }.values().forEach { it.clear() }
	}
	fun setPressed(btn: InputButton, now: Int) {
		this.buttonTimers[btn]!!.setPressed(now)
	}
	fun addButton(btn: InputButton) {if (btn !in buttonTimers) buttonTimers[btn] = ButtonTimer()}
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

open class Textfield(val text: StrValue, widthInCharacters: Int, pos: Pos, init: Textfield.() -> Unit) : Widget(pos) {

	val isActive: Boolean
		get() = this.id == app.active_widget_id

	var disabled: Boolean = false
	var variant = Variant.DEFAULT
	var margin = 5
	var defaultText = ""
	var onChange: (()->Unit)? = null
	var hover = false
		private set
		get() {
			return app.mousePos.is_in_rect(pos, Pos(width, height))
		}
	override var width = widthInCharacters * app.skin.charWidth
	override var height = app.skin.rowHeight
		private set

	{
		additionalIdInfo = text.hashCode().toString()
		init()
	}

	val isCursorShown: Boolean
		get() = (app.getWidgetData("Textfield", id) as TextfieldData).isCursorShown
	val cursorPos: Int
		get() = (app.getWidgetData("Textfield", id) as TextfieldData).cursorPos

	override fun draw() {
		app.skin.drawTextfield(this)
	}

	override fun handleEvents() {
		val was_hot = app.hot_widget_id == id
		val was_active = app.active_widget_id == id
		val justClicked = !disabled && app.leftMouseButton.down && hover && !was_active
		if (justClicked) {
			app.active_widget_id = id
		}

		if (hover && !was_hot) {
			app.hot_widget_id = id
		} else if (was_hot && !hover) {
			app.hot_widget_id = null
		}

		val active = app.active_widget_id == id
		val data = getOrCreateMyData()
		if (!active) {
			return
		}
		val clicked = app.leftMouseButton.down && hover
		if (clicked) {
			data.cursorPos = (app.mousePos.x - (this.pos.x + app.skin.panelBorder)) / app.skin.charWidth
			data.cursorPos = data.cursorPos.at_most(text.value.length)
		}
		handleInput(data, app.pressedChar)

		if (data.nextCursorToggleTick <= app.currentTick) {
			data.isCursorShown = !data.isCursorShown
			data.nextCursorToggleTick = app.currentTick + 700
		}
	}

	private fun handleInput(data: TextfieldData, pressedChar: Char?) {
		val now = app.currentTick
		//println("$now: handleInput")
		val beforeValue = text.value
		if (app.backspace.down && data.cursorPos > 0 && data.isPressable(app.backspace, now)) {
			handleBackspace(data)
			data.clearButtonsExcept(app.backspace)
			data.setPressed(app.backspace, now)
		} else if (app.leftArrow.down && data.cursorPos > 0 && data.isPressable(app.leftArrow, now)) {
			println("$now: left pressed")
			data.cursorPos--
			data.clearButtonsExcept(app.leftArrow)
			data.setPressed(app.leftArrow, now)
		} else if (app.rightArrow.down && data.cursorPos < text.value.length && data.isPressable(app.rightArrow, now)) {
			data.cursorPos++
			data.clearButtonsExcept(app.rightArrow)
			data.setPressed(app.rightArrow, now)
		} else if (app.home.down) {
			data.clearButtonsExcept(null)
			data.cursorPos = 0
		} else if (app.end.down) {
			data.clearButtonsExcept(null)
			data.cursorPos = text.value.length
		} else if (pressedChar != null) {
			data.clearButtonsExcept(null)
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

	protected fun getOrCreateMyData(): TextfieldData {
		val dataPtr = app.getWidgetData("Textfield", id)
		return if (dataPtr == null) {
			val data = TextfieldData()
			app.setWidgetData("Textfield", id, data)
			data
		} else {
			dataPtr as TextfieldData
		}
	}
}