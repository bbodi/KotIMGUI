package widget

import skin.Variant
import timeline.StrValue
import timeline.IntValue
import timeline.AppSizeMetricData
import timeline.AppState
import org.junit.Test
import kotlin.test.assertEquals
import skin.Skin
import timeline.Keys

data class NumberFieldData(val text: StrValue)

open class NumberField(val value: IntValue, width: Int, pos: Pos, metrics: AppSizeMetricData, init: NumberField.() -> Unit = {}) : Textfield(StrValue(value.value.toString()), width + 2, pos, metrics, {}) {
	var max: Int? = null
	var min: Int? = null
	var valueLabels: Array<String> = array()

	;
	{
		additionalIdInfo = value.hashCode().toString()
		init()
		if (valueLabels.size != 0) {
			min = 0
			max = valueLabels.size - 1
		}
	}

	val upperButton = Button("▴", this.pos + Pos(this.width - (metrics.charWidth*3), 0), metrics, {
		height = metrics.rowHeight/2
	})
	val lowerButton = Button("▾", this.pos + Pos(this.width - (metrics.charWidth*3), metrics.rowHeight/2), metrics, {
		height = metrics.rowHeight/2
	})

	override fun draw(skin: Skin) {
		if (valueLabels.size != 0) {
			text.value = valueLabels[value.value]
		}
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
		super<Textfield>.handleEvents(state)
		value.value = safeParseInt(text.value) ?: value.value
		upperButton.handleEvents(state)
		lowerButton.handleEvents(state)
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
			text.value = afterValue.toString()
			if (onChange != null) {
				onChange!!()
			}
		}
	}

	private fun decreaseValue() {
		if (min == null || value.value > min!!) {
			value.value--
		}
	}

	private fun increaseValue() {
		if (max == null || value.value < max!!) {
			value.value++
		}
	}
}