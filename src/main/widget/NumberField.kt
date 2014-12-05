package widget

import skin.Variant
import timeline.StrValue
import timeline.IntValue
import timeline.AppSizeMetricData
import timeline.AppState
import org.junit.Test
import kotlin.test.assertEquals
import skin.Skin

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

	override protected fun handlePressedChar(data: TextfieldData, pressedChar: Char) {
		if (pressedChar in '0' .. '9') {
			insertChar(data, pressedChar)
		}
	}

	override fun handleEvents(state: AppState) {
		super<Textfield>.handleEvents(state)
		value.value = safeParseInt(text.value) ?: value.value
		upperButton.handleEvents(state)
		lowerButton.handleEvents(state)
		val beforeValue = value.value
		if (upperButton.down && state.isPressable(state.upArrow)) {
			increaseValue()
			state.clearButtonsExcept(state.upArrow)
			state.setPressed(state.upArrow)
		} else if (lowerButton.down && state.isPressable(state.downArrow)) {
			decreaseValue()
			state.clearButtonsExcept(state.downArrow)
			state.setPressed(state.downArrow)
		} else if (state.upArrow.down && state.isActive(this) && state.isPressable(state.upArrow)) {
			increaseValue()
			upperButton.down = true
			state.clearButtonsExcept(state.upArrow)
			state.setPressed(state.upArrow)
		} else if (state.downArrow.down && state.isActive(this) && state.isPressable(state.downArrow)) {
			decreaseValue()
			lowerButton.down = true
			state.clearButtonsExcept(state.downArrow)
			state.setPressed(state.downArrow)
		}
		val afterValue = value.value
		if (beforeValue != afterValue && onChange != null) {
			onChange!!()
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