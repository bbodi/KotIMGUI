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

class EnumNumberField<T: Enum<T>>(val enumPtr: Ptr<T>, val enumValues: Array<T>, width: Int, pos: Pos, metrics: AppSizeMetricData, init: EnumNumberField<T>.()->Unit = {}) :
		NumberField<Int>(Ptr(enumPtr.value.ordinal()), width + 2, pos, metrics) {
	private val valueLabels: Array<String>
	{
		init()
		min = 0
		max = enumValues.size - 1
		valueLabels = enumValues.map { it.toString() }.copyToArray()
		textPtr.value = enumPtr.value.toString()
	}

	override protected fun getShownText(): String {
		return enumPtr.value.toString()
	}

	override protected fun handlePressedChar(pressedChar: Char) {
	}

	override protected fun onTextChanged() {
	}

	override protected fun decreaseValue() {
		if (value.value > min!!) {
			value.value--
			enumPtr.value = enumValues[value.value]
		}
	}

	override protected fun increaseValue() {
		if (value.value < max!!) {
			value.value++
			enumPtr.value = enumValues[value.value]
		}
	}
}