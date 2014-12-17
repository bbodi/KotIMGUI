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
import widget.ButtonTimer
import java.util.HashMap
import skin.Font

val canvas: HTMLCanvasElement
	get() {
		return window.document.getElementsByTagName("canvas").item(0) as HTMLCanvasElement
	}

private val context: CanvasContext
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
private var global_keyDownEvents: MutableList<Int> = arrayListOf<Int>()

fun keyDown(pressedChar: Char?, keyCode: Int) {
	if (pressedChar != null) {
		timeline.global_pressedChar = pressedChar
	} else {
		global_keyDownEvents.add(keyCode)
	}
}

var global_leftMouseDown = false;
var global_middleMouseDown = false;
var global_rightMouseDown = false;

fun onMouseDown(which: Int) {
	when (which ) {
		1 -> global_leftMouseDown = true
		2 -> global_middleMouseDown = true
		3 -> global_rightMouseDown = true
	}
}

fun onMouseUp(which: Int) {
	when (which ) {
		1 -> global_leftMouseDown = false
		2 -> global_middleMouseDown = false
		3 -> global_rightMouseDown = false
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

public class AppSizeMetricData (
		val font: Font,
		val rowHeight: Int,
		val textMarginY: Int,
		val charWidth: Int,
		val charHeight: Int,
		val panelBorder: Int
)

public enum class Keys(val keyCode: Int) {
	LeftArrow: Keys(37)
	RightArrow: Keys(39)
	UpArrow: Keys(38)
	DownArrow: Keys(40)
	Backspace: Keys(8)
	Enter: Keys(13)
	Ctrl: Keys(17)
	Alt: Keys(18)
	Shift: Keys(16)
	Tab: Keys(9)
	Home: Keys(36)
	End: Keys(35)
	PageUp: Keys(33)
	PageDown: Keys(34)
	Del: Keys(46)
}

public class AppState(val metrics: AppSizeMetricData) {

	var mouseScrollDelta: Int = 0
	var mousePos = Pos(0, 0)
	val leftMouseButton = InputButton()
	val rightMouseButton = InputButton()
	val middleMouseButton = InputButton()
	var pressedChar: Char? = null
	var active_widget_id: Any? = null
	var hot_widget_id: Any? = null

	var currentTick = 10000

	fun isActive(widget: Widget) = widget.id == active_widget_id

	private val chars = hashMapOf<Char, InputButton>()
	private val keys: Map<Keys, InputButton> = Keys.values().map { Pair(it, InputButton()) }.toMap()
	private val buttonTimers: Map<Keys, ButtonTimer> = Keys.values().map { Pair(it, ButtonTimer()) }.toMap()
	private val widgetData = hashMapOf<Any, Any>()

	fun isPressable(key: Keys) = this.buttonTimers[key]!!.isPressable(currentTick)
	fun clearKeysExcept(key: Keys?) {
		buttonTimers.filter { it.key != key }.values().forEach { it.clear() }
	}

	fun setPressed(key: Keys) {
		this.buttonTimers[key]!!.setPressed(currentTick)
	}

	fun isJustPressed(char: Char): Boolean = chars[char]?.just_pressed ?: false
	fun isJustReleased(char: Char): Boolean = chars[char]?.just_released ?: false
	fun isDown(char: Char): Boolean = chars[char]?.down ?: false
	fun updateChar(char: Char, down: Boolean) {
		if (down && char !in chars) {
			chars[char] = InputButton()
		}
		chars[char]?.update(down)
	}

	fun isJustPressed(key: Keys): Boolean = keys[key]!!.just_pressed
	fun isJustReleased(key: Keys): Boolean = keys[key]!!.just_released
	fun isKeyDown(key: Keys): Boolean = keys[key]!!.down
	fun updateKey(key: Keys, down: Boolean) {
		keys[key]!!.update(down)
	}

	fun disableAllKeysExcept(key: Char?) {
		chars.toList().filter({ it.first != key }).forEach { it.second.update(false) }
	}

	fun getWidgetData(className: String, id: Int): Any? {
		return widgetData[className + id.toString()]
	}

	fun setWidgetData(className: String, id: Int, data: Any) {
		widgetData[className + id.toString()] = data
	}

	fun update() {
		currentTick += 40
		leftMouseButton.update(global_leftMouseDown)
		rightMouseButton.update(global_rightMouseDown)
		middleMouseButton.update(global_middleMouseDown)
		this.mouseScrollDelta = globalMouseScrollDelta
	}

	fun setPressedKeys(pressedKeys: List<Int>, pressedChar: Char?) {
		Keys.values().forEach { keyName ->
			val down = pressedKeys.contains(keyName.keyCode)
			updateKey(keyName, down)
		}
		if (pressedChar != null) {
			updateChar(pressedChar, true)
		}
		disableAllKeysExcept(pressedChar)
		this.pressedChar = pressedChar
	}
}

abstract class Application(val skin: Skin) {

	abstract fun doFrame()

	val appState = AppState(skin.getAppSizeMetricData())


	fun frame() {
		appState.update()

		appState.setPressedKeys(global_keyDownEvents, global_pressedChar)
		global_keyDownEvents.clear()
		global_pressedChar = null

		skin.clear()
		setCursor(CursorStyle.Default);
		doFrame()
		showDebugPanel()
		debugLines.clear()
		globalMouseScrollDelta = 0
		requestAnimationFrame({ frame() })
	}

	{
		jq {
			val onlyTests = window.document.getElementsByTagName("canvas").item(0).attributes.getNamedItem("only_tests") != null
			if (!onlyTests) {
				jq(canvas).mousemove {
					appState.mousePos = mousePos(it)
				}

				requestAnimationFrame({ frame() })
			}
		}
	}

	fun showDebugPanel() {
		if (appState.isJustReleased('h')) {
			showDebugLines = !showDebugLines
		}
		if (showDebugLines) {
			debugLines.add("mousePos: ${appState.mousePos}")

			Panel(appState.mousePos, appState.metrics, {
				debugLines.withIndices().forEach {
					val pos = if (it.first == 0) downAlongLeftMargin() else downAlongLeftMargin()
					when (it.second) {
						is String -> +Label(it.second as String, pos, appState.metrics)
						//is Ptr<*> -> +NumberField(it.second as IntValue, 4, pos, appState.metrics)
					}
				}
			}).drawAndHandleEvents(appState, context, skin)
		}
	}
}