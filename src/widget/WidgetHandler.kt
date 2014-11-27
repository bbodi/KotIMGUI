
package widget

import timeline.InputButton
import timeline.context
import skin.Skin
import timeline.widgetHandler

class WidgetHandler(val skin: Skin) {
	var active_widget_id: Any? = null
	var hot_widget_id: Any? = null
	var mousePos = AbsolutePos(0, 0)
	val widgetDatas = hashMapOf<Any, Any>()
	val leftMouseButton = InputButton()
	val rightMouseButton = InputButton()
	val middleMouseButton = InputButton()
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

	var lastDrawnPos = AbsolutePos(0, 0)
	var lastDrawnWidget: Widget? = null

	{
		for (ch in 'a'..'z') {
			keys.put(ch, InputButton())
		}
	}

	fun getAbsolutePos(widget: Widget, widgetPos: Pos): AbsolutePos {
		val w = if (lastDrawnWidget == null) 0 else lastDrawnWidget!!.width
		val h = if (lastDrawnWidget == null) 0 else lastDrawnWidget!!.height
		val realPos = when(widgetPos) {
			is AbsolutePos -> widgetPos : AbsolutePos
			is RelativePos -> lastDrawnPos.add(widgetPos, w, h)
			else -> throw IllegalArgumentException()
		}
		lastDrawnWidget = widget
		lastDrawnPos = realPos
		return realPos
	}

	fun clear() {
		skin.clear()
		lastDrawnPos = AbsolutePos(0, 0)
		lastDrawnWidget = null
	}
}