package skin

import widget.Button
import widget.WidgetHandler
import widget.HScrollBar
import widget.VScrollBar
import widget.Panel
import widget.Textfield
import timeline.context


trait Skin {
	val rowHeight: Int
	val charWidth: Int
	val charHeight: Int

	fun clear()

	fun drawButton(widgetHandler: WidgetHandler, widget: Button)
	fun calcButtonSize(button: Button)

	fun drawHorizontalScrollbar(widgetHandler: WidgetHandler, widget: HScrollBar)
	fun drawVerticalScrollbar(widgetHandler: WidgetHandler, widget: VScrollBar)
	fun drawPanel(widgetHandler: WidgetHandler, widget: Panel)

	fun drawTextfield(widgetHandler: WidgetHandler, widget: Textfield)
	fun calcTextFieldSize(textfield: Textfield)
}

public enum class Variant {
	DEFAULT
	SUCCESS
	WARNING
	DANGER
	INFO
}

public data class ColorStates(val normal: String, val hover: String, val active: String)

data class Color(val r: Int, val g: Int, val b: Int) {
	override fun toString(): String = "rgb($r, $g, $b)"
}

public enum class FontModifier {
	NONE
	BOLD
	ITALIC
}
data class Font(val size: Int, val font: String, val mod: FontModifier = FontModifier.NONE) {
	override fun toString(): String {
		val modStr = when (mod) {
			FontModifier.BOLD -> "bold"
			FontModifier.ITALIC -> "italic"
			FontModifier.NONE -> ""
		}
		return "$modStr ${size}pt $font"
	}
}