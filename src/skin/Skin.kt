package skin

import widget.Button
import widget.HScrollBar
import widget.VScrollBar
import widget.Panel
import widget.Textfield
import timeline.context
import widget.ActionItem
import widget.ActionMenu
import widget.Checkbox
import widget.RadioButton
import widget.TabPanel
import widget.Label
import timeline.Application


trait Skin {
	val font: Font
	val rowHeight: Int
	val textMarginY: Int
	val charWidth: Int
	val charHeight: Int
	val panelBorder: Int

	fun clear()

	fun drawButton(widget: Button)

	fun drawHorizontalScrollbar(app: Application, widget: HScrollBar)
	fun drawVerticalScrollbar(app: Application, widget: VScrollBar)

	fun drawTextfield(widget: Textfield)

	fun drawActionItem(actionItem: ActionItem)
	fun drawActionMenu(actionMenu: ActionMenu)

	fun drawCheckbox(checkbox: Checkbox)

	fun drawRadioButton(radioButton: RadioButton)
	fun drawTabPanel(widget: TabPanel)
	fun drawPanelRect(x: Int,
					  y: Int,
					  w: Int,
					  h: Int,
					  variant: Variant)

	fun drawLabel(widget: Label)
	fun drawMiniButton(widget: Button)


	public fun text(text: String, x: Number, y: Number, color: String, font: Font) {
		context.fillStyle = color;
		context.font = font.toString()
		context.textBaseline = "hanging"
		context.fillText(text, x, y)
	}
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