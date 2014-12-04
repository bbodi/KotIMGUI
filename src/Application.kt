package timeline

import java.util.ArrayList
import jquery.*
import kotlin.js.dom.html.window
import kotlin.js.dom.html.HTMLElement
import kotlin.js.dom.html.HTMLImageElement
import kotlin.js.dom.html5.CanvasContext
import kotlin.js.dom.html5.CanvasGradient
import kotlin.js.dom.html5.HTMLCanvasElement
import widget.Pos
import widget.Button
import widget.VScrollBar
import widget.HScrollBar
import skin.Variant
import widget.Panel
import widget.Pos
import widget.Textfield
import skin.DiscoverUI
import widget.ActionItem
import widget.ActionMenu
import widget.Widget
import widget.Checkbox
import widget.RadioButton
import widget.TabPanel
import timeline.Timeline
import widget.chart.LineChart
import widget.Label
import widget.NumberField
import widget.chart.FloatsAndSinkersChart
import skin.Skin

val canvas: HTMLCanvasElement
	get() {
		return window.document.getElementsByTagName("canvas").item(0) as HTMLCanvasElement
	}

val context: CanvasContext
	get() {
		return canvas.getContext("2d")!!
	}

fun mousePos(e: MouseEvent): Pos {
	var offset = Pos(0, 0)
	var element: HTMLElement? = canvas
	while (element != null) {
		val el: HTMLElement = element!!
		offset += Pos(el.offsetLeft.toInt(), el.offsetTop.toInt())
		element = el.offsetParent
	}
	return Pos(e.pageX.toInt(), e.pageY.toInt()) - offset
}

class InputButton {
	var down: Boolean = false
		private set
	var just_pressed: Boolean = false
		private set
	var just_released: Boolean = false
		private set

	fun update(is_currently_down: Boolean) {
		if (is_currently_down && !down) {
			just_pressed = true;
			just_released = false;
			down = true;
		} else if (!is_currently_down && down) {
			just_pressed = false;
			just_released = true;
			down = false;
		} else {
			just_pressed = false;
			just_released = false;
			down = is_currently_down;
		}
	}
}

private var global_pressedChar: Char? = null
private var keyDownEvents: MutableList<Int> = arrayListOf<Int>()
private var keyUpEvents: MutableList<Int> = arrayListOf<Int>()

fun keyDown(pressedChar: Char?, keyCode: Int) {
	keyDownEvents.add(keyCode)
	if (pressedChar != null) {
		timeline.global_pressedChar = pressedChar
	}
}

fun keyUp(keyCode: Int) {
	keyUpEvents.add(keyCode)
}


fun onMouseDown(which: Int) {
	when (which ) {
		1 -> leftMouseDown = true
		2 -> middleMouseDown = true
		3 -> rightMouseDown = true
	}
}

fun onMouseUp(which: Int) {
	when (which ) {
		1 -> leftMouseDown = false
		2 -> middleMouseDown = false
		3 -> rightMouseDown = false
	}
}

native
fun requestAnimationFrame(func: Any) {

}

private var showDebugLines = false
public val debugLines: MutableList<Any> = arrayListOf<Any>()
private var globalMouseScrollDelta: Int = 0
fun onMouseScroll(delta: Int) {
	globalMouseScrollDelta = delta
}

abstract class Application(val skin: Skin) {

	abstract fun doFrame()

	var mouseScrollDelta: Int = 0
	var mousePos = Pos(0, 0)
	var active_widget_id: Any? = null
	var hot_widget_id: Any? = null

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

	fun getWidgetData(className: String, id: Int): Any? {
		return widgetDatas[className + id.toString()]
	}

	fun setWidgetData(className: String, id: Int, data: Any){
		widgetDatas[className + id.toString()] = data
	}

	fun frame() {
		currentTick += 40
		leftMouseButton.update(leftMouseDown)
		rightMouseButton.update(rightMouseDown)
		middleMouseButton.update(middleMouseDown)
		this.mouseScrollDelta = globalMouseScrollDelta
		handleKeys()
		skin.clear()
		setCursor(CursorStyle.Default);
		doFrame()
		showDebugPanel()
		global_pressedChar = null
		debugLines.clear()
		globalMouseScrollDelta = 0
		requestAnimationFrame({ frame() })
	}

	{
		jq {
			jq(canvas).mousemove {
				mousePos = mousePos(it)
			}

			requestAnimationFrame({ frame() })
		}
	}

	fun handleKeys() {
		val asd = {(array: List<Int>, down: Boolean) ->
			array.forEach {
				when (it) {
					38 -> upArrow.update(down)
					37 -> leftArrow.update(down)
					40 -> downArrow.update(down)
					39 -> rightArrow.update(down)
					33 -> pageUp.update(down)
					34 -> pageDown.update(down)
					13 -> enter.update(down)
					17 -> ctrl.update(down)
					18 -> alt.update(down)
					9 -> tab.update(down)
					36 -> home.update(down)
					35 -> end.update(down)
					8 -> backspace.update(down)
					16 -> shift.update(down)
				}
			}
		}
		asd(keyDownEvents, true)
		asd(keyUpEvents, false)
		keyUpEvents.clear()
		keyDownEvents.clear()

		if (global_pressedChar != null) {
			updateKey(global_pressedChar!!, true)
		}
		disableAllKeysExcept(global_pressedChar)
		pressedChar = global_pressedChar
	}

	fun showDebugPanel() {
		if (showDebugLines) {
			Panel(mousePos, {
				debugLines.withIndices().forEach {
					val pos = if (it.first == 0) downUnderMargin() else downAlongLeftMargin()
					when (it.second) {
						is String -> +Label(it.second as String, pos)
						is IntValue -> +NumberField(it.second as IntValue, 4, pos)
					}
				}
			}).drawAndHandleEvents()
		}
	}
}