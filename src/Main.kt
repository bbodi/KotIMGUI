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
import widget.WidgetHandler
import widget.Button
import widget.VScrollBar
import widget.HScrollBar
import skin.Variant
import widget.Panel
import widget.AbsolutePos
import widget.RelativePos
import widget.fromLastWidgetBottom
import widget.Textfield
import skin.DiscoverUI
import widget.ActionItem
import widget.ActionMenu
import widget.fromLastWidgetBottomLeft
import widget.Widget
import widget.Checkbox
import widget.RadioButton
import widget.TabPanel
import timeline.Timeline
import widget.chart.LineChart
import widget.Label

fun getImage(path: String): HTMLImageElement {
	val image = window.document.createElement("img") as HTMLImageElement
	image.src = path
	return image
}

val canvas: HTMLCanvasElement
	get() {
		return window.document.getElementsByTagName("canvas").item(0) as HTMLCanvasElement
	}

val context: CanvasContext
	get() {
		return canvas.getContext("2d")!!
	}

fun mousePos(e: MouseEvent): AbsolutePos {
	var offset = AbsolutePos(0, 0)
	var element: HTMLElement? = canvas
	while (element != null) {
		val el: HTMLElement = element!!
		offset += AbsolutePos(el.offsetLeft.toInt(), el.offsetTop.toInt())
		element = el.offsetParent
	}
	return AbsolutePos(e.pageX.toInt(), e.pageY.toInt()) - offset
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

public val widgetHandler: WidgetHandler = WidgetHandler(DiscoverUI(1397, 796, 3))
var pressedChar: Char? = null
var keyCode: Int? = null
fun setPressedKeysFromJavascript(pressedChar: Char?, keyCode: Int) {
	timeline.pressedChar = pressedChar
	timeline.keyCode = keyCode
}

fun handleKeys() {
	widgetHandler.upArrow.update(keyCode == 38)
	widgetHandler.leftArrow.update(keyCode == 37)
	widgetHandler.downArrow.update(keyCode == 40)
	widgetHandler.rightArrow.update(keyCode == 39)
	widgetHandler.pageUp.update(keyCode == 33)
	widgetHandler.pageDown.update(keyCode == 34)
	widgetHandler.enter.update(keyCode == 13)
	widgetHandler.ctrl.update(keyCode == 17)
	widgetHandler.alt.update(keyCode == 18)
	widgetHandler.tab.update(keyCode == 9)
	widgetHandler.home.update(keyCode == 36)
	widgetHandler.end.update(keyCode == 35)
	widgetHandler.backspace.update(keyCode == 8)
	widgetHandler.shift.update(keyCode == 16)
	for (ch in 'a'..'z') {
		widgetHandler.keys[ch]!!.update(ch == pressedChar)
	}
	widgetHandler.pressedChar = pressedChar
}

val value = IntValue(50)
val zoom_value = IntValue(50)
val strValue = StrValue("")
val strValue1 = StrValue("")
val strValue2 = StrValue("")
val strValue3 = StrValue("")
val strValue4 = StrValue("")
val strValue5 = StrValue("")
val booleanValues = array<BooleanValue>(BooleanValue(true), BooleanValue(false), BooleanValue(false), BooleanValue(false), BooleanValue(false))
val radioButtonValue = IntValue(0)
val tabPanelValue = IntValue(0)
val booleanValue = BooleanValue(true)
var leftMouseDown = false;
var middleMouseDown = false;
var rightMouseDown = false;
var showActionMenu = false

var actionMenuPos = AbsolutePos(0, 0)
val graphData = array(init_data(), init_data(), init_data(), init_data(), init_data())
val graphAvgData = array(calc_ema(graphData[0], 0.9f), calc_ema(graphData[1], 0.9f), calc_ema(graphData[2], 0.9f), calc_ema(graphData[3], 0.9f), calc_ema(graphData[4], 0.9f))

fun String.allocNew(): String {
	return StringBuilder().append(this).toString()
}

fun init_data(): MutableList<Float> {
	var last = 30.0f;
	val data = ArrayList<Float>(100000);
	for (i in 0..100000) {
		last = last + Math.random().toFloat() * 2.0f - 1.0f;
		if (last < 0) {
			last = 30f;
		} else if (last > 60f) {
			last = 30f;
		}
		data.add(last);
	}
	return data;
}


fun calc_ema(data: List<Float>, smoothingConstant: Float) {
	val avgData = ArrayList<Float>(data.size)
	var last_data = data[0];
	for ((i, v) in data.withIndices().filter { pair -> pair.first > 0 }) {
		val smoothing_percentage = 1f - smoothingConstant;
		val curr_data = last_data + smoothing_percentage * (v - last_data);
		avgData.add(curr_data);
		last_data = curr_data;
	}
}

fun doFrame() {
	widgetHandler.currentTick += 40
	widgetHandler.leftMouseButton.update(leftMouseDown)
	widgetHandler.rightMouseButton.update(rightMouseDown)
	widgetHandler.middleMouseButton.update(middleMouseDown)
	handleKeys()
	pressedChar = null
	keyCode = null
	widgetHandler.clear()
	setCursor(CursorStyle.Default);
	Panel(AbsolutePos(70, 100), {
		+Button("Default Button", downAlongLeftMargin(10), {
			width = 200
			variant = Variant.DEFAULT
			onClick = {
				strValue.data = strValue.data + "Default"
			}
		})
		+Button("Green Button", downAlongLeftMargin(20), {
			width = 200
			variant = Variant.SUCCESS
			onClick = {
				strValue.data = strValue.data + "Green"
			}
		})
		+Button("Red Button", downAlongLeftMargin(20), {
			width = 200
			variant = Variant.DANGER
		})
		+Button("Yellow Button", downAlongLeftMargin(20), {
			width = 200
			variant = Variant.WARNING
		})
		+Button("Info Button", downAlongLeftMargin(20), {
			width = 200
			variant = Variant.INFO
		})
		+Button("Button".allocNew(), downAlongLeftMargin(20), {
			width = 200
			variant = Variant.WARNING
		})
		+Button("Button".allocNew(), downAlongLeftMargin(20), {
			width = 200
			variant = Variant.INFO
		})
		+Button("Inactive Button", downAlongLeftMargin(20), {
			width = 200
			disabled = true
		})
	}).drawAndHandleEvents()

	Panel(AbsolutePos(300, 50), {
		+Textfield(strValue, downAlongLeftMargin(10), {
			width = 200
			variant = Variant.DEFAULT
		})
		+Textfield(strValue1, downAlongLeftMargin(10), {
			width = 200
			variant = Variant.INFO
		})
		+Textfield(strValue2, downAlongLeftMargin(), {
			width = 200
			variant = Variant.WARNING
		})
		+Textfield(strValue3, downAlongLeftMargin(20), {
			width = 200
			variant = Variant.DANGER
		})
		+Textfield(strValue4, downAlongLeftMargin(20), {
			width = 200
			variant = Variant.SUCCESS
		})
		+Textfield(strValue5, downAlongLeftMargin(20), {
			width = 200
			disabled = true
		})
	}).drawAndHandleEvents()

	Panel(AbsolutePos(300, 430), {
		+Checkbox("Default", booleanValues[0], downAlongLeftMargin(10))
		+Checkbox("Info", booleanValues[1], downAlongLeftMargin(10), {
			variant = Variant.INFO
		})
		+Checkbox("Warning", booleanValues[2], downAlongLeftMargin(10), {
			variant = Variant.WARNING
		})
		+Checkbox("Error", booleanValues[3], downAlongLeftMargin(10), {
			variant = Variant.DANGER
		})
		+Checkbox("Success", booleanValues[4], downAlongLeftMargin(10), {
			variant = Variant.SUCCESS
		})
		+Checkbox("Disabled", booleanValues[0], downAlongLeftMargin(10), {
			disabled = true
		})
	}).drawAndHandleEvents()

	Panel(AbsolutePos(500, 430), {
		+RadioButton("Default", radioButtonValue, 0, downAlongLeftMargin(10))
		+RadioButton("Info", radioButtonValue, 1, downAlongLeftMargin(10), {
			variant = Variant.INFO
		})
		+RadioButton("Warning", radioButtonValue, 2, downAlongLeftMargin(10), {
			variant = Variant.WARNING
		})
		+RadioButton("Danger", radioButtonValue, 3, downAlongLeftMargin(10), {
			variant = Variant.DANGER
		})
		+RadioButton("Success", radioButtonValue, 4, downAlongLeftMargin(10), {
			variant = Variant.SUCCESS
		})
		+RadioButton("Disabled", radioButtonValue, 5, downAlongLeftMargin(10), {
			disabled = true
		})
	}).drawAndHandleEvents()

	tabPanel()


	HScrollBar(value, AbsolutePos(470, 400), {
		postfix = "%"
	}).drawAndHandleEvents()
	VScrollBar(zoom_value, AbsolutePos(300, 500), {
		postfix = "%"
	}).drawAndHandleEvents()

	if (widgetHandler.rightMouseButton.just_pressed) {
		showActionMenu = true
		actionMenuPos = widgetHandler.mousePos
	}

	if (showActionMenu) {
		var parentActionItem: ActionItem? = null
		val contextMenu = ActionMenu(actionMenuPos, {
			+ActionItem(downAlongLeftMargin(margin), {
				label = "Normal"
				comment = "Ctrl+N"
			})
			+ActionItem(downAlongLeftMargin(1), {
				label = "Disabled"
				disabled = true
			})
			+ActionItem(downAlongLeftMargin(1), {
				label = "Checkbox value"
				checkBoxValue = booleanValue
			})
			parentActionItem = ActionItem(downAlongLeftMargin(1), {
				label = "Parent"
				hasSubMenu = true
			})
			+parentActionItem!!
			+Textfield(strValue, downAlongLeftMargin(), {
				width = 200
			})
			+Button("Start", downAlongLeftMargin(), {
				width = 200
			})
		})
		contextMenu.drawAndHandleEvents()

		val subMenu = ActionMenu(parentActionItem!!.pos + AbsolutePos(20, 20), {
			+ActionItem(downAlongLeftMargin(1), {
				label = "Sub Normal"
			})
			+ActionItem(downAlongLeftMargin(1), {
				label = "Sub Disabled"
				disabled = true
			})
			+ActionItem(downAlongLeftMargin(1), {
				label = "Sub Checkbox value"
				checkBoxValue = booleanValue
			})
			+Textfield(strValue, downAlongLeftMargin(), {
				width = 200
			})
			+Button("Sub Start", downAlongLeftMargin(), {
				width = 200
			})
		})
		subMenu.handleEvents()
		val showSubActionMenu = parentActionItem!!.hover || subMenu.hover
		if (showSubActionMenu) {
			subMenu.draw()
		}
		if (widgetHandler.leftMouseButton.just_released) {
			showActionMenu = contextMenu.hover || showSubActionMenu
		}
	}
	if (widgetHandler.keys['h']!!.just_released) {
		showDebugLines = !showDebugLines
	}
	if (showDebugLines) {
		Panel(widgetHandler.mousePos, {
			debugLines.withIndices().forEach {
				+Label(it.second, if (it.first == 0) downUnderMargin() else  downAlongLeftMargin())
			}
		}).drawAndHandleEvents()
	}
	debugLines.clear()
	widgetHandler.mouseScrollDelta = 0
	requestAnimationFrame({doFrame()})
}

var showDebugLines = false
val debugLines = arrayListOf<String>()

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

fun onMouseScroll(delta: Int) {
	widgetHandler.mouseScrollDelta = delta
}

native
fun requestAnimationFrame(func: Any) {

}

fun main(args: Array<String>) {
	jq {
		jq(canvas).mousemove {
			widgetHandler.mousePos = mousePos(it)
		}

		requestAnimationFrame({doFrame()})
	}
}

class IntValue(var data: Int) : Comparable<Int> by data {}
// Ez egyelőre nem működik, JS hiba keletkezik a CharSequence miatt
//class StrValue(var data: String) : Comparable<String> by data, CharSequence by data {}
class StrValue(var data: String) : Comparable<String> by data {}

class BooleanValue(var data: Boolean) {}


/* Javascript hívási példa!!
JS-be elég egy key nevű metódus
native("key")
fun callJavascript() {}
*/

native
public class Date(ms: Int) {
	public fun getTime() : Int = noImpl
	native("getDate")
	public fun getDayOfMonth() : Int = noImpl
	native("getDay")
	public fun getDayOfWeek() : Int = noImpl
	public fun getFullYear() : Int = noImpl
	public fun getHours() : Int = noImpl
	public fun getMilliseconds() : Int = noImpl
	public fun getMinutes(): Int = noImpl
	public fun getMonth(): Int = noImpl
	public fun getSeconds(): Int = noImpl
}

fun Int.at_most(max: Int): Int {
	return Math.min(this, max)
}

fun Int.at_least(min: Int): Int {
	return Math.max(this, min)
}

fun Int.limit_into(min: Int, max: Int): Int {
	return this.at_least(min).at_most(max)
}

fun tabPanel() {
	TabPanel(tabPanelValue, AbsolutePos(700, 20), {
		height = 600
		addTabPanelItem("Buttons")
		addTabPanelItem("Textfields")
		addTabPanelItem("Checkboxes", { variant = Variant.SUCCESS })
		addTabPanelItem("Radioboxes")
		addTabPanelItem("Graph", { variant = Variant.DANGER })
		addTabPanelItem("Disabled", { disabled = true })
		if (tabPanelValue.data == 0) {
			+Panel(downAlongLeftMargin(10), {
				+Button("Default Button", downAlongLeftMargin(10), {
					width = 200
					variant = Variant.DEFAULT
					onClick = {
						strValue.data = strValue.data + "Default"
					}
				})
				+Button("Green Button", downAlongLeftMargin(20), {
					width = 200
					variant = Variant.SUCCESS
					onClick = {
						strValue.data = strValue.data + "Green"
					}
				})
				+Button("Red Button", downAlongLeftMargin(20), {
					width = 200
					variant = Variant.DANGER
				})
				+Button("Yellow Button", downAlongLeftMargin(20), {
					width = 200
					variant = Variant.WARNING
				})
				+Button("Info Button", downAlongLeftMargin(20), {
					width = 200
					variant = Variant.INFO
				})
				+Button("Button".allocNew(), downAlongLeftMargin(20), {
					width = 200
					variant = Variant.WARNING
				})
				+Button("Button".allocNew(), downAlongLeftMargin(20), {
					width = 200
					variant = Variant.INFO
				})
				+Button("Inactive Button", downAlongLeftMargin(20), {
					width = 200
					disabled = true
				})
			})
		} else if (tabPanelValue.data == 1) {
			+Panel(downAlongLeftMargin(10), {
				+Textfield(strValue, downAlongLeftMargin(10), {
					width = 200
					variant = Variant.DEFAULT
				})
				+Textfield(strValue1, downAlongLeftMargin(10), {
					width = 200
					variant = Variant.INFO
				})
				+Textfield(strValue2, downAlongLeftMargin(), {
					width = 200
					variant = Variant.WARNING
				})
				+Textfield(strValue3, downAlongLeftMargin(20), {
					width = 200
					variant = Variant.DANGER
				})
				+Textfield(strValue4, downAlongLeftMargin(20), {
					width = 200
					variant = Variant.SUCCESS
				})
				+Textfield(strValue5, downAlongLeftMargin(20), {
					width = 200
					disabled = true
				})
			})
		} else if (tabPanelValue.data == 2) {
			+Panel(downAlongLeftMargin(10), {
				+Checkbox("Default", booleanValues[0], downAlongLeftMargin(10))
				+Checkbox("Info", booleanValues[1], downAlongLeftMargin(10), {
					variant = Variant.INFO
				})
				+Checkbox("Warning", booleanValues[2], downAlongLeftMargin(10), {
					variant = Variant.WARNING
				})
				+Checkbox("Error", booleanValues[3], downAlongLeftMargin(10), {
					variant = Variant.DANGER
				})
				+Checkbox("Success", booleanValues[4], downAlongLeftMargin(10), {
					variant = Variant.SUCCESS
				})
				+Checkbox("Disabled", booleanValues[0], downAlongLeftMargin(10), {
					disabled = true
				})
			})
		} else if (tabPanelValue.data == 3) {
			+Panel(downAlongLeftMargin(10), {
				+RadioButton("Default", radioButtonValue, 0, downAlongLeftMargin(10))
				+RadioButton("Info", radioButtonValue, 1, downAlongLeftMargin(10), {
					variant = Variant.INFO
				})
				+RadioButton("Warning", radioButtonValue, 2, downAlongLeftMargin(10), {
					variant = Variant.WARNING
				})
				+RadioButton("Danger", radioButtonValue, 3, downAlongLeftMargin(10), {
					variant = Variant.DANGER
				})
				+RadioButton("Success", radioButtonValue, 4, downAlongLeftMargin(10), {
					variant = Variant.SUCCESS
				})
				+RadioButton("Disabled", radioButtonValue, 5, downAlongLeftMargin(10), {
					disabled = true
				})
			})
		} else if (tabPanelValue.data == 4) {
			+Timeline(downAlongLeftMargin(10), {
				booleanValues.withIndices().forEach {
					if (it.second.data) {
						+LineChart(graphData[it.first], {
							color = when (Variant.values()[it.first]) {
								Variant.INFO -> "#29A1D3"
								Variant.DEFAULT -> "#525864"
								Variant.SUCCESS -> "#8AB71C"
								Variant.WARNING -> "#F1B018"
								Variant.DANGER -> "#EE4E10"
							}
						})
					}
				}
			})
		}
	}).drawAndHandleEvents()
}