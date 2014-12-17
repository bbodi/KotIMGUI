package widget

import skin.Variant
import timeline.AppSizeMetricData
import timeline.AppState
import org.junit.Test
import kotlin.test.assertEquals
import skin.Skin
import timeline.Keys
import kotlin.properties.Delegates
import kotlin.js.dom.html5.CanvasContext
import timeline.Ptr

data class FloatNumberFieldData(val backendText: Ptr<String>) : TextfieldData()

open class FloatNumberField(value: Ptr<Float>, width: Int, pos: Pos, metrics: AppSizeMetricData,
							init: FloatNumberField.() -> Unit = {}) : NumberField<Float>(Ptr(value.value), width + 4, pos, metrics) {
	{
		init()
	}
	private var data: FloatNumberFieldData = FloatNumberFieldData(Ptr("Fakk"))

	override protected fun handlePressedChar(pressedChar: Char) {
		if (pressedChar in '0' .. '9' || pressedChar == '.') {
			insertChar(pressedChar)
		}
	}

	override protected fun onTextChanged() {
		data.backendText.value = textPtr.value
		val newVal = safeParseDouble(data.backendText.value)?.toFloat()
		if (newVal != null) {
			value.value = newVal
		}
	}

	override protected fun getShownText(): String {
		val backendText = data.backendText.value
		val index = backendText.indexOf('.')
		return if (index != -1) {
			backendText.substring(0, index+3)
		} else {
			backendText
		}
	}

	override protected fun getOrCreateMyData(state: AppState): Pair<Boolean, FloatNumberFieldData> {
		val dataPtr = state.getWidgetData("FloatNumberField", id)
		return if (dataPtr == null) {
			val data = FloatNumberFieldData(Ptr(textPtr.value))
			state.setWidgetData("FloatNumberField", id, data)
			Pair(true, data)
		} else {
			Pair(false, dataPtr as FloatNumberFieldData)
		}
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