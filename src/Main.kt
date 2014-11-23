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
import skin.DarkUi
import widget.Button
import skin.fill_rect
import skin.text
import widget.VScrollBar
import widget.HScrollBar
import skin.Variant
import widget.Panel
import widget.AbsolutePos
import widget.RelativePos
import widget.downFromLastWidget
import widget.Textfield

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

class TimeLineWindow {
	var avg_data: MutableList<Float> = ArrayList<Float>()
	var data: MutableList<Float> = init_data();
	var smoothing_constant = 0.9f;
	var zoom_level = 100f;
	var pos = 10000;

	class object {
		fun init_data(): MutableList<Float> {
			var last = 30.0f;
			val data = ArrayList<Float>(10000);
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
	}

	fun calc_ema() {
		avg_data.clear();
		var last_data = this.data[0];
		for ((i, v) in data.enumerate().filter { pair -> pair.index > 0 }) {
			val smoothing_percentage = 1f - this.smoothing_constant;
			val curr_data = last_data + smoothing_percentage * (v - last_data);
			avg_data.add(curr_data);
			last_data = curr_data;
		}
	}

	fun draw(mouse_pos: Pos) {
		val a = (zoom_level / Math.sqrt(3.0));
		val x1 = pos - a;
		val x2 = pos + a;
		val range_w = (x2 - x1);
		val y1 = 300;
		val y2 = 1500;
		val range_h = (y2 - y1);
		val widget_x = 0;
		val widget_y = 0;
		val widget_w = 800;
		val widget_h = 600;
		val bottom = (widget_y + widget_h) - (widget_h * 0.1f);
		val screen_step_w = 800f / (2f * a);
		val screen_step_h = 600f / (range_h);
		val trend_value_rect_size = 5;

		for ((i, v) in (x1..x2).enumerate()) {
			val data_index = (x1 + i).toInt()
			val real_value = (data[data_index] * 10f).toInt()
			val trend = (avg_data[data_index] * 10f).toInt()
			val color = if (real_value > trend) {
				"red"
			} else {
				"green"
			}
			val real_y = bottom - 10 - real_value
			val trend_y = bottom - 10 - trend
			var x = (widget_x + i * screen_step_w).toInt()
			fill_rect(
					x - trend_value_rect_size / 2,
					trend_y - trend_value_rect_size / 2,
					trend_value_rect_size,
					trend_value_rect_size,
					color
			);
		}
		draw_asd(mouse_pos)
	}

	fun draw_asd(mouse_pos: Pos) {
		val a = (zoom_level / Math.sqrt(3.0));
		val x1 = pos - a;
		val x2 = pos + a;
		val range_w = (x2 - x1);
		val y1 = 300;
		val y2 = 1500;
		val range_h = (y2 - y1);
		val widget_x = 0;
		val widget_y = 0;
		val widget_w = 800;
		val widget_h = 600;
		val bottom = (widget_y + widget_h) - (widget_h * 0.1f);
		val screen_step_w = 800f / (2f * a);
		val screen_step_h = 600f / (range_h);
		val trend_value_rect_size = 5;

		val mouse_index = (mouse_pos.x / screen_step_w).toInt();
		val real_value = (data[mouse_index] * 10f).toInt();
		val trend = (avg_data[mouse_index] * 10f).toInt();
		val txt = "$real_value, $trend";
		var x = (0 + mouse_index * screen_step_w).toInt();

		val trend_y = bottom - 10 - trend;
		fill_rect(x-15, trend_y-60, txt.length*30, 30, "red");
		text(txt, x-15, trend_y-60, "white", Font())
	}

	fun clear() {
		context.fillStyle = "#2E3138"
		context.fillRect(0, 0, 800, 600)
		context.strokeStyle = "#000000"
		context.lineWidth = 4.0
		context.strokeRect(0, 0, 800, 600)
	}
}

class EnumerateStream<T>(val iter: EnumerateIterator<T>) : Stream<IteratorPair<T>> {
	override fun iterator(): Iterator<IteratorPair<T>> {
		return iter
	}
}

class EnumerateIterator<T>(val iter: Iterator<T>) : Iterator<IteratorPair<T>> {
	override fun hasNext(): Boolean {
		return iter.iterator().hasNext()
	}

	var index = 0
	override fun next(): IteratorPair<T> {
		index++
		return IteratorPair(index, iter.iterator().next())
	}
}

fun <T> Iterable<T>.enumerate(): EnumerateStream<T> {
	return EnumerateStream(EnumerateIterator(this.iterator()))
}

fun <T> List<T>.reversed(): List<T> {
	val result = ArrayList<T>()
	var i = size()
	while (i > 0) {
		result.add(get(--i))
	}
	return result
}

public class Pair<A, B> (
		public val first: A,
		public val second: B
) {
	public fun component1(): A = first
	public fun component2(): B = second

	override fun toString(): String = "($first, $second)"
}

public class IteratorPair<T> (
		public val index: Int,
		public val value: T
) {
	override fun toString(): String = "($index, $value)"
	fun component1(): Int = index
	fun component2(): T = value
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

val widgetHandler = WidgetHandler(DarkUi(15))
var pressedChar: Char? = null
var keyCode: Int? = null
fun setPressedKeysFromJavascript(pressedChar: Char?, keyCode: Int) {
	println("$pressedChar, $keyCode")
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
	for (ch in 'a'..'Z') {
		widgetHandler.keys[ch]!!.update(ch == pressedChar)
	}
	widgetHandler.pressedChar = pressedChar
}

fun main(args: Array<String>) {
	val value = IntValue(50)
	val zoom_value = IntValue(50)
	val strValue = StrValue("")
	jq {
		val timeLineWindow = TimeLineWindow()
		timeLineWindow.calc_ema()
		timeLineWindow.draw(widgetHandler.mouse_pos);
		var mouse_down = false;
		jq(canvas).mousedown {
			widgetHandler.mouse_pos = mousePos(it)
			mouse_down = true;
		}
		jq(canvas).mouseup {
			widgetHandler.mouse_pos = mousePos(it)
			mouse_down = false;
		}
		jq(canvas).mousemove {
			widgetHandler.mouse_pos = mousePos(it)
		}

		window.setInterval({
			widgetHandler.currentTick += 40
			widgetHandler.leftMouseButton.update(mouse_down)
			handleKeys()
			pressedChar = null
			keyCode = null
			if (widgetHandler.leftMouseButton.down) {
				if (widgetHandler.mouse_pos.x < 400) {
					timeLineWindow.pos -= 1
				} else {
					timeLineWindow.pos += 1
				}
			}
			timeLineWindow.clear()
			timeLineWindow.draw(widgetHandler.mouse_pos)
			Panel(widgetHandler, {
				pos = AbsolutePos(70, 100)
				+Button(widgetHandler, {
					width = 200
					label = "BLUE BUTTON"
					variant = Variant.DEFAULT
				})
				+Button(widgetHandler, {
					pos = downFromLastWidget(20)
					width = 200
					label = "GREEN BUTTON"
					variant = Variant.SUCCESS
				})
				+Button(widgetHandler, {
					pos = downFromLastWidget(20)
					width = 200
					label = "RED BUTTON"
					variant = Variant.DANGER
				})
				+Button(widgetHandler, {
					pos = downFromLastWidget(20)
					width = 200
					label = "YELLOW BUTTON"
					variant = Variant.WARNING
				})
				+Button(widgetHandler, {
					pos = downFromLastWidget(20)
					width = 200
					label = "DARK BUTTON"
					variant = Variant.DARK
				})
				+Button(widgetHandler, {
					pos = downFromLastWidget(20)
					width = 200
					label = "INACTIVE BUTTON"
					disabled = true
				})
			}).drawAndHandleEvents()

			Panel(widgetHandler, {
				pos = AbsolutePos(500, 50)
				+Textfield(strValue, widgetHandler, {
					width = 200
					variant = Variant.DEFAULT
				})
				+Textfield(strValue, widgetHandler, {
					width = 200
					pos = downFromLastWidget(20)
					variant = Variant.DEFAULT
				})
				+Textfield(strValue, widgetHandler, {
					width = 200
					pos = downFromLastWidget(20)
					variant = Variant.WARNING
				})
				+Textfield(strValue, widgetHandler, {
					pos = downFromLastWidget(20)
					width = 200
					variant = Variant.DANGER
				})
				+Textfield(strValue, widgetHandler, {
					pos = downFromLastWidget(20)
					width = 200
					variant = Variant.SUCCESS
				})
				+Textfield(strValue, widgetHandler, {
					pos = downFromLastWidget(20)
					width = 200
					variant = Variant.DARK
				})
				+Textfield(strValue, widgetHandler, {
					pos = downFromLastWidget(20)
					width = 200
					disabled = true
				})
			}).drawAndHandleEvents()


			HScrollBar(widgetHandler, value, {
				pos = AbsolutePos(470, 400)
				postfix = "%"
			}).drawAndHandleEvents()
			VScrollBar(widgetHandler, zoom_value, {
				pos = AbsolutePos(300, 500)
				postfix = "%"
			}).drawAndHandleEvents()
		}, 40);
	}
}

class IntValue(var data: Int) : Comparable<Int> by data {}
// Ez egyelőre nem működik, JS hiba keletkezik a CharSequence miatt
//class StrValue(var data: String) : Comparable<String> by data, CharSequence by data {}
class StrValue(var data: String) : Comparable<String> by data {}

/* Javascript hívási példa!!
JS-be elég egy key nevű metódus
native("key")
fun callJavascript() {}
*/

fun Int.at_most(max: Int): Int {
	return Math.min(this, max)
}

fun Int.at_least(min: Int): Int {
	return Math.max(this, min)
}

fun Int.limit_into(min: Int, max: Int): Int {
	return this.at_least(min).at_most(max)
}

data class Color(val r: Int, val g: Int, val b: Int) {
	override fun toString(): String = "rgb($r, $g, $b)"
}

public enum class FontModifier {
	NONE
	BOLD
	ITALIC
}
data class Font(val size: Int=15, val font: String="Courier New", val mod: FontModifier = FontModifier.NONE) {
	override fun toString(): String {
		val modStr = when (mod) {
			FontModifier.BOLD -> "bold"
			FontModifier.ITALIC -> "italic"
			FontModifier.NONE -> ""
		}
		return "$modStr ${size}pt $font"
	}
}