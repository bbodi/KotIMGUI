package widget

import skin.Variant
import timeline.AppSizeMetricData
import timeline.AppState
import org.junit.Test
import kotlin.test.assertEquals
import skin.Skin
import timeline.Keys
import kotlin.js.dom.html5.CanvasContext
import timeline.Ptr

abstract class NumberField<T : Number>(val value: Ptr<T>, width: Int, pos: Pos, val metrics: AppSizeMetricData) : Textfield(Ptr(value.value.toString()), width + 2, pos, metrics, {}) {
	var max: T? = null
	var min: T? = null

	val upperButton = Button("▴", this.pos + Pos(this.width - (metrics.charWidth*3), 0), metrics, {
		height = metrics.rowHeight/2
	})
	val lowerButton = Button("▾", this.pos + Pos(this.width - (metrics.charWidth*3), metrics.rowHeight/2), metrics, {
		height = metrics.rowHeight/2
	})

	override protected fun isHover(state: AppState): Boolean {
		return state.mousePos.isInRect(pos, Pos(width - metrics.charWidth*3, height))
	}

	override protected fun isActive(state: AppState): Boolean {
		return state.isActive(this) || upperButton.clicked || lowerButton.clicked
	}

	override fun draw(context: CanvasContext, skin: Skin) {
		skin.drawTextfield(this)
		skin.drawMiniButton(upperButton)
		skin.drawMiniButton(lowerButton)
	}

	override protected fun handlePressedChar(pressedChar: Char) {
		if (pressedChar in '0' .. '9') {
			insertChar(pressedChar)
		}
	}

	override fun handleEvents(state: AppState) {
		upperButton.handleEvents(state)
		lowerButton.handleEvents(state)
		super<Textfield>.handleEvents(state)
	}

	override protected fun handleInput(pressedChar: Char?, state: AppState) {
		super<Textfield>.handleInput(pressedChar, state)
		val beforeValue = value.value
		if (upperButton.clicked) {
			increaseValue()
			state.clearKeysExcept(Keys.UpArrow)
			state.setPressed(Keys.UpArrow)
		} else if (lowerButton.clicked) {
			decreaseValue()
			state.clearKeysExcept(Keys.DownArrow)
			state.setPressed(Keys.DownArrow)
		} else if (state.isKeyDown(Keys.UpArrow) && state.isActive(this) && state.isPressable(Keys.UpArrow)) {
			increaseValue()
			upperButton.down = true
			state.clearKeysExcept(Keys.UpArrow)
			state.setPressed(Keys.UpArrow)
		} else if (state.isKeyDown(Keys.DownArrow) && state.isActive(this) && state.isPressable(Keys.DownArrow)) {
			decreaseValue()
			lowerButton.down = true
			state.clearKeysExcept(Keys.DownArrow)
			state.setPressed(Keys.DownArrow)
		}
		val afterValue = value.value
		if (beforeValue != afterValue) {
			this.textPtr.value = afterValue.toString()
		}
	}

	abstract protected fun decreaseValue()
	abstract protected fun increaseValue()
}