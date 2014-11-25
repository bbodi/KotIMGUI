package skin

import widget.Button
import widget.WidgetHandler
import widget.HScrollBar
import widget.VScrollBar
import widget.Panel
import widget.Textfield
import timeline.context
import widget.ActionItem
import widget.ActionMenu


trait Skin {
	val rowHeight: Int
	val charWidth: Int
	val charHeight: Int

	fun clear()

	fun drawButton(widget: Button)
	fun calcButtonSize(button: Button)

	fun drawHorizontalScrollbar(widgetHandler: WidgetHandler, widget: HScrollBar)
	fun drawVerticalScrollbar(widgetHandler: WidgetHandler, widget: VScrollBar)
	fun drawPanel(widget: Panel)

	fun drawTextfield(widget: Textfield)
	fun calcTextFieldSize(textfield: Textfield)

	fun calcActionItemSize(actionItem: ActionItem)
	fun drawActionItem(actionItem: ActionItem)
	fun drawActionMenu(actionMenu: ActionMenu)

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