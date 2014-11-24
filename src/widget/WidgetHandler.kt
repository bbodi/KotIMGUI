package widget

import timeline.InputButton
import timeline.context
import skin.Skin
import timeline.widgetHandler

class WidgetHandler(val skin: Skin) {
	var active_widget_id: Any? = null
	var hot_widget_id: Any? = null
	var mouse_pos = AbsolutePos(0, 0)
	val widgetDatas = hashMapOf<Any, Any>()
	val leftMouseButton = InputButton()
	val leftArrow = InputButton()
	val rightArrow = InputButton()
	val upArrow = InputButton()
	val downArrow = InputButton()

	val backspace = InputButton()
	val enter = InputButton()
	val ctrl = InputButton()
	val alt = InputButton()
	val shift = InputButton()
	val tab = InputButton()

	val home = InputButton()
	val end = InputButton()
	val pageUp = InputButton()
	val pageDown = InputButton()
	val keys = hashMapOf<Char, InputButton>()
	var pressedChar: Char? = null

	var currentTick = 0

	{
		for (ch in 'a'..'Z') {
			widgetHandler.keys.put(ch, InputButton())
		}
	}
}