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

class IntNumberField(value: Ptr<Int>, width: Int, pos: Pos, metrics: AppSizeMetricData, init: IntNumberField.()->Unit = {}) :
		NumberField<Int>(Ptr(value.value), width + 2, pos, metrics) {
	{
		init()
	}

	override protected fun handlePressedChar(pressedChar: Char) {
		if (pressedChar in '0' .. '9') {
			insertChar(pressedChar)
		}
	}

	override protected fun onTextChanged() {

	}

	override protected fun decreaseValue() {
		if (min == null || value.value > min!!) {
			value.value--
		}
	}

	override protected fun increaseValue() {
		if (max == null || value.value < max!!) {
			value.value++
		}
	}
}