package widget

import skin.Variant
import timeline.StrValue
import timeline.widgetHandler
import timeline.IntValue

class NumberField(val value: IntValue, width: Int, pos: Pos, init: Textfield.() -> Unit = {}) : Textfield(StrValue(value.data.toString()), width + 2, pos, init) {

	override val id: Int = PositionBasedId(this.pos.x, this.pos.y, value.hashCode()).hashCode()

	val upperButton = Button("▴", this.pos + AbsolutePos(this.width - widgetHandler.skin.charWidth, 0), {
		height = widgetHandler.skin.rowHeight/2
	})
	val lowerButton = Button("▾", this.pos + AbsolutePos(this.width - widgetHandler.skin.charWidth, widgetHandler.skin.rowHeight/2), {
		height = widgetHandler.skin.rowHeight/2
	})

	override fun draw() {
		widgetHandler.skin.drawTextfield(this)
		widgetHandler.skin.drawMiniButton(upperButton)
		widgetHandler.skin.drawMiniButton(lowerButton)
	}

	override protected fun handlePressedChar(data: TextfieldData, pressedChar: Char) {
		if (pressedChar in '0' .. '9') {
			insertChar(data, pressedChar)
		}
	}

	override fun handleEvents() {
		super<Textfield>.handleEvents()
		value.data = safeParseInt(text.data) ?: value.data
		upperButton.handleEvents()
		lowerButton.handleEvents()
		if (upperButton.down) {
			value.data++
		} else if (lowerButton.down) {
			value.data--
		} else if (widgetHandler.upArrow.down && isActive) {
			value.data++
			upperButton.down = true
		} else if (widgetHandler.downArrow.down && isActive) {
			value.data--
			lowerButton.down = true
		}

	}
}