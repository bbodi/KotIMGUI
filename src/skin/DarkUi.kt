package skin

import widget.Button
import widget.WidgetHandler
import timeline.context
import widget.HScrollBar
import widget.VScrollBar
import widget.Panel
import widget.Textfield

public class DarkUi(val width: Int, val height: Int, override val rowHeight: Int) : Skin {

	val margin = 2
	override val charHeight = rowHeight - margin*2
	{
		context.font = "${charHeight}pt Courier New"
	}
	override val charWidth = (context.measureText("M")!!.width+0.5).toInt()

	override fun clear() {
		context.fillStyle = "#2E3138"
		context.fillRect(0, 0, width, height)
		context.strokeStyle = "#000000"
		context.lineWidth = 4.0
		context.strokeRect(0, 0, width, height)
	}

	fun getColor(variant: Variant): ColorStates {
		return when (variant) {
			Variant.INFO -> ColorStates("#29A1D3", "#58B4DB", "#2B7B9E")
			Variant.DEFAULT -> ColorStates("#525864", "#6B6B6B", "#2B2525")
			Variant.SUCCESS -> ColorStates("#8AB71C", "#9BBC45", "#637C1D")
			Variant.WARNING -> ColorStates("#F1B018", "#F7C44C", "#B78A21")
			Variant.DANGER -> ColorStates("#EE4E10", "#F47344", "#B5441B")
		}
	}

	override fun calcButtonSize(button: Button) {
		button.height = rowHeight
		button.width = if (width == 0) button.label.length * charWidth + margin*2 else width
	}

	override fun drawButton(widgetHandler: WidgetHandler, widget: Button) {
		val (normalColor, hoverColor, activeColor) = getColor(widget.variant)
		val w = widget.width
		val h = widget.height
		val x = widget.pos.x
		val y = widget.pos.y
		if (widget.disabled) {
			fill_rounded_rect(x, y, w, h, 5, "#525864")
		} else if (widget.down) {
			fill_rounded_rect(x, y, w, h, 5, activeColor)
		} else if (widget.hover) {
			fill_rounded_rect(x, y, w, h, 5, hoverColor)
		} else {
			fill_rounded_rect(x, y, w, h, 5, normalColor)
		}
		val label_w = context.measureText(widget.label)!!.width
		val textX = x + widget.margin + widget.width / 2 - label_w / 2
		val textY = y + rowHeight / 4
		text(widget.label.toUpperCase(), textX, textY, "white", Font(charHeight, "Courier New", mod=FontModifier.BOLD))
	}

	override fun calcTextFieldSize(textfield: Textfield) {
		textfield.height = 2 * rowHeight
	}

	override fun drawTextfield(widgetHandler: WidgetHandler, widget: Textfield) {
		val x = widget.pos.x
		val y = widget.pos.y
		val w = widget.width
		val h = widget.height

		val isActive = widget.id == widgetHandler.active_widget_id
		val (normalColor, hoverColor, activeColor) = getColor(widget.variant)
		fill_rounded_rect(x, y, w, h, 5, "#24252A")
		if (widget.variant != Variant.DEFAULT || isActive) {
			stroke_rounded_rect(x, y, w, h, 5, normalColor)
		}

		val textX = x + widget.margin
		val textY = y + rowHeight / 4
		text(widget.text.data, textX, textY, "white", Font(charHeight, "Courier New"))
		if (widget.isCursorShown && isActive) {
			text("_", textX + charWidth * widget.cursorPos, textY, "white", Font(charHeight, "Courier New"))
		}
	}

	override fun drawHorizontalScrollbar(widgetHandler: WidgetHandler, widget: HScrollBar) {
		val x = widget.pos.x
		val y = widget.pos.y
		fill_rounded_rect(x, y + rowHeight / 4, widget.width, rowHeight / 2, rowHeight / 4, "#24252A")
		val value = widget.value.data
		val value_range = widget.max_value - widget.min_value
		val value_percent = (value - widget.min_value) / value_range.toDouble()
		val orange_bar_w = widget.width * value_percent
		fill_rounded_rect(x, y + rowHeight / 4, orange_bar_w.toInt(), rowHeight / 2, rowHeight / 4, "#EE4E10")

		context.save()
		context.beginPath();
		context.arc(x + orange_bar_w, y + rowHeight / 2, rowHeight / 2, 0, 2 * Math.PI, false);
		context.fillStyle = "#F7F8F3"
		context.fill()
		context.closePath()
		context.restore()
	}

	override fun drawVerticalScrollbar(widgetHandler: WidgetHandler, widget: VScrollBar) {
		val x = widget.pos.x
		val y = widget.pos.y
		fill_rounded_rect(x + charWidth / 4, y, charWidth / 2, widget.height, charWidth / 4, "#24252A")
		val value = widget.value.data
		val value_range = widget.max_value - widget.min_value
		val value_percent = (value - widget.min_value) / value_range.toDouble()
		val orange_bar_h = widget.height * value_percent
		fill_rounded_rect(x + charWidth / 4, y, charWidth / 2, orange_bar_h.toInt(), charWidth / 4, "#EE4E10")

		context.save()
		context.beginPath();
		context.arc(x + charWidth / 2, y + orange_bar_h, charWidth / 2, 0, 2 * Math.PI, false);
		context.fillStyle = "#F7F8F3";
		context.fill();
		context.closePath()
		context.restore()
	}

	override fun drawPanel(widgetHandler: WidgetHandler, widget: Panel) {
		val x = widget.pos.x
		val y = widget.pos.y
		fill_rounded_rect(x, y, widget.width, widget.height, 5, "#454954")
	}


	private fun fill_rect(x: Number, y: Number, w: Number, h: Number, color: String) {
		//context.rect(x, y, w, h)
		context.fillStyle = color
		//context.fill()
		context.fillRect(x, y, w, h);
	}

	private fun gradient(x: Number, y: Number, w: Number, h: Number, c1: String, c2: String) {
		var grd = context.createLinearGradient(x, y, w, h)!!
		grd.addColorStop(0, c1)
		grd.addColorStop(1, c2)
		context.fillStyle = grd
		//context.fill()
		context.fillRect(x, y, w, h);
	}

	private fun stroke_rounded_rect(x: Int, y: Int, w: Int, h: Int, radius: Int, color: String) {
		context.save()
		context.strokeStyle = color;
		context.beginPath();
		// draw top and top right corner
		context.moveTo(x + radius, y);
		context.arcTo(x + w, y, x + w, y + radius, radius);

		// draw right side and bottom right corner
		context.arcTo(x + w, y + h, x + w - radius, y + h, radius);

		// draw bottom and bottom left corner
		context.arcTo(x, y + h, x, y + h - radius, radius);

		// draw left and top left corner
		context.arcTo(x, y, x + radius, y, radius);

		context.lineWidth = 1.0
		context.stroke()
		context.restore()
	}

	private fun fill_rounded_rect(x: Int, y: Int, w: Int, h: Int, radius: Int, color: String) {
		context.save()
		context.fillStyle = color;
		context.beginPath();
		// draw top and top right corner
		context.moveTo(x + radius, y);
		context.arcTo(x + w, y, x + w, y + radius, radius);

		// draw right side and bottom right corner
		context.arcTo(x + w, y + h, x + w - radius, y + h, radius);

		// draw bottom and bottom left corner
		context.arcTo(x, y + h, x, y + h - radius, radius);

		// draw left and top left corner
		context.arcTo(x, y, x + radius, y, radius);

		context.fill()
		context.restore()
	}

	private fun text(text: String, x: Number, y: Number, color: String, font: Font) {
		/*context.fillStyle = color;
		context.font = "15pt Courier New"
		context.fillText(text, x, y);*/

		context.fillStyle = color;
		context.font = font.toString()
		context.textBaseline = "top"
		context.fillText(text, x, y)
	}
}