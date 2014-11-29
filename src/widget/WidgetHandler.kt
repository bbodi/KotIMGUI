
package widget

import timeline.InputButton
import timeline.context
import skin.Skin
import timeline.widgetHandler

class WidgetHandler(val skin: Skin) {
	var mouseScrollDelta: Int = 0

	var active_widget_id: Any? = null
	var hot_widget_id: Any? = null
	var mousePos = AbsolutePos(0, 0)
	private val widgetDatas = hashMapOf<Any, Any>()
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
	private val keys = hashMapOf<Char, InputButton>()
	var pressedChar: Char? = null

	var currentTick = 0

	var lastDrawnPos = AbsolutePos(0, 0)
	var lastDrawnWidget: Widget? = null

	fun isJustPressed(key: Char): Boolean = keys[key]?.just_pressed ?: false
	fun isJustReleased(key: Char): Boolean = keys[key]?.just_released ?: false
	fun isDown(key: Char): Boolean = keys[key]?.down ?: false
	fun updateKey(key: Char, down: Boolean) {
		if (down && key !in keys) {
			keys[key] = InputButton()
		}
		keys[key]?.update(down)
	}
	fun disableAllKeysExcept(key: Char?) {
		keys.toList().filter({ it.first != key }).forEach { it.second.update(false) }
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

	fun getWidgetData(className: String, id: Int): Any? {
		return widgetDatas[className + id.toString()]
	}

	fun setWidgetData(className: String, id: Int, data: Any){
		widgetDatas[className + id.toString()] = data
	}
}