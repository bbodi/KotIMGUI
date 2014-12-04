package widget

import skin.Variant
import timeline.StrValue
import timeline.app
import timeline.IntValue

data class NumberFieldData(val text: StrValue)

open class NumberField(val value: IntValue, width: Int, pos: Pos, init: NumberField.() -> Unit = {}) : Textfield(StrValue(value.value.toString()), width + 2, pos, {}) {
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
		val data = getOrCreateMyData()
		data.addButton(app.upArrow)
		data.addButton(app.downArrow)
	}

	val upperButton = Button("▴", this.pos + Pos(this.width - (app.skin.charWidth*3), 0), {
		height = app.skin.rowHeight/2
	})
	val lowerButton = Button("▾", this.pos + Pos(this.width - (app.skin.charWidth*3), app.skin.rowHeight/2), {
		height = app.skin.rowHeight/2
	})

	override fun draw() {
		if (valueLabels.size != 0) {
			text.value = valueLabels[value.value]
		}
		app.skin.drawTextfield(this)
		app.skin.drawMiniButton(upperButton)
		app.skin.drawMiniButton(lowerButton)
	}

	override protected fun handlePressedChar(data: TextfieldData, pressedChar: Char) {
		if (pressedChar in '0' .. '9') {
			insertChar(data, pressedChar)
		}
	}

	override fun handleEvents() {
		super<Textfield>.handleEvents()
		val data = getOrCreateMyData()
		value.value = safeParseInt(text.value) ?: value.value
		upperButton.handleEvents()
		lowerButton.handleEvents()
		val now = app.currentTick
		val beforeValue = value.value
		if (upperButton.down && data.isPressable(app.upArrow, now)) {
			increaseValue()
			data.clearButtonsExcept(app.upArrow)
			data.setPressed(app.upArrow, now)
		} else if (lowerButton.down && data.isPressable(app.downArrow, now)) {
			decreaseValue()
			data.clearButtonsExcept(app.downArrow)
			data.setPressed(app.downArrow, now)
		} else if (app.upArrow.down && isActive && data.isPressable(app.upArrow, now)) {
			increaseValue()
			upperButton.down = true
			data.clearButtonsExcept(app.upArrow)
			data.setPressed(app.upArrow, now)
		} else if (app.downArrow.down && isActive && data.isPressable(app.downArrow, now)) {
			decreaseValue()
			lowerButton.down = true
			data.clearButtonsExcept(app.downArrow)
			data.setPressed(app.downArrow, now)
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