package skin

import widget.Button
import widget.WidgetHandler
import timeline.context
import widget.HScrollBar
import widget.VScrollBar
import widget.Panel
import widget.Textfield
import timeline.setCursor
import timeline.CursorStyle
import widget.ActionItem
import widget.ActionMenu
import widget.AbsolutePos

public class DiscoverUI(val width: Int, val height: Int, val rowHeightPercent: Int) : Skin {

	override val rowHeight = (height * (rowHeightPercent / 100.0)+0.5).toInt()
	val margin = 5
	val textMarginY = rowHeight/4
	override val charHeight = rowHeight - textMarginY*2
	val font = Font(charHeight, "Courier New");
	override val charWidth: Int

	{
		context.font = font.toString()
		charWidth = (context.measureText("M")!!.width+0.5).toInt()
	}

	override fun clear() {
		context.fillStyle = "#2C2C2C"
		context.fillRect(0, 0, width, height)
		context.strokeStyle = "#000000"
		context.lineWidth = 4.0
		context.strokeRect(0, 0, width, height)
	}

	fun getTextfieldStrokeColor(variant: Variant): ColorStates {
		return when(variant) {
			Variant.INFO -> ColorStates("#29A1D3", "#58B4DB", "#2B7B9E")
			Variant.DEFAULT -> ColorStates("#525864", "#6B6B6B", "#2B2525")
			Variant.SUCCESS -> ColorStates("#8AB71C", "#9BBC45", "#637C1D")
			Variant.WARNING -> ColorStates("#F1B018", "#F7C44C", "#B78A21")
			Variant.DANGER -> ColorStates("#EE4E10", "#F47344", "#B5441B")
		}
	}

	override fun drawButton(widget: Button) {
		val w = widget.width
		val h = widget.height
		val x = widget.pos.x
		val y = widget.pos.y
		drawButtonRect(x, y, w, h, widget.variant, widget.disabled, widget.hover, widget.down)
		val label_w = widget.label.length * charWidth
		val textX = x + margin + widget.width / 2 - label_w/2
		val textY = y + textMarginY
		text(widget.label, textX, textY, "white", font)
		if (widget.hover) {
			if (widget.disabled) {
				setCursor(CursorStyle.NotAllowed);
			} else {
				setCursor(CursorStyle.Pointer);
			}
		}
	}

	override fun drawTextfield(widget: Textfield) {
		val x = widget.pos.x
		val y = widget.pos.y
		val w = widget.width
		val h = widget.height

		val isActive = widget.isActive
		fill_rounded_rect(x, y, w, h, 5, "#24252A")
		if (widget.variant != Variant.DEFAULT) {
			val (normalColor, hoverColor, activeColor) = getTextfieldStrokeColor(widget.variant)
			stroke_rounded_rect(x, y, w, h, 5, normalColor)
		} else if (isActive) {
			val (normalColor, hoverColor, activeColor) = getTextfieldStrokeColor(Variant.INFO)
			stroke_rounded_rect(x, y, w, h, 5, normalColor)
		}

		val textX = x + margin
		val textY = y + textMarginY
		text(widget.text.data, textX, textY, "white", font)
		if (widget.isCursorShown && isActive) {
			text("_", textX + charWidth*widget.cursorPos, textY, "white", font)
		}
	}

	override fun drawActionItem(actionItem: ActionItem) {
		val x = actionItem.pos.x
		val y = actionItem.pos.y
		val w = actionItem.parent!!.width - 2*actionItem.parent!!.margin
		val textX = x + margin
		val textY = y + textMarginY
		val textColor = if (actionItem.disabled) {"#8C8C8C"} else {"white"}
		if (!actionItem.disabled && actionItem.hover) {
			fillRect(x, y, w, actionItem.height, "#2C2C2C")
		}
		text(actionItem.label, textX, textY, textColor, font)
		if (actionItem.hasSubMenu) {
			text("▸", x + w - charWidth, textY, textColor, font)
		}
	}

	override fun drawHorizontalScrollbar(widgetHandler: WidgetHandler, widget: HScrollBar) {
		val x = widget.pos.x
		val y = widget.pos.y
		fill_rounded_rect(x, y+ rowHeight /4, widget.width, rowHeight /2, rowHeight /4, "#24252A")
		val value = widget.value.data
		val value_range = widget.max_value - widget.min_value
		val value_percent = (value - widget.min_value) / value_range.toDouble()
		val orange_bar_w = widget.width * value_percent
		fill_rounded_rect(x, y+ rowHeight /4, orange_bar_w.toInt(), rowHeight /2, rowHeight /4, "#EE4E10")

		context.save()
		context.beginPath();
		context.arc(x+orange_bar_w, y+ rowHeight /2, rowHeight /2, 0, 2 * Math.PI, false);
		context.fillStyle = "#F7F8F3"
		context.fill()
		context.closePath()
		context.restore()
	}

	override fun drawVerticalScrollbar(widgetHandler: WidgetHandler, widget: VScrollBar) {
		val x = widget.pos.x
		val y = widget.pos.y
		fill_rounded_rect(x + charWidth/4, y, charWidth/2, widget.height, charWidth/4, "#24252A")
		val value = widget.value.data
		val value_range = widget.max_value - widget.min_value
		val value_percent = (value - widget.min_value) / value_range.toDouble()
		val orange_bar_h = widget.height * value_percent
		fill_rounded_rect(x+charWidth/4, y, charWidth/2, orange_bar_h.toInt(), charWidth/4, "#EE4E10")

		context.save()
		context.beginPath();
		context.arc(x+charWidth/2, y+orange_bar_h, charWidth/2, 0, 2 * Math.PI, false);
		context.fillStyle = "#F7F8F3";
		context.fill();
		context.closePath()
		context.restore()
	}

	override fun drawPanel(widget: Panel) {
		val x = widget.pos.x
		val y = widget.pos.y
		val w = widget.width
		val h = widget.height
		fillRect(x, y, w, h, "#434343") // outer bg
		strokeRect(x, y, w, h, "black") // outer line
		fillRect(x+margin, y + margin, w - margin*2, h - margin*2, "#323232") // inner bg
		strokeRect(x+margin, y + margin, w - margin*2, h - margin*2, "black") // outer line
	}

	override fun drawActionMenu(actionMenu: ActionMenu) {
		val x = actionMenu.pos.x
		val y = actionMenu.pos.y
		fillStrokeRoundedRect(x, y, actionMenu.width, actionMenu.height, "#4A4A4A", "black")
	}

	private fun fillRect(x: Number, y: Number, w: Number, h: Number, color: String) {
		context.save()
		context.fillStyle = color
		context.fillRect(x, y, w, h);
		context.restore()
	}

	private fun strokeRect(x: Number, y: Number, w: Number, h: Number, color: String) {
		context.save()
		context.lineWidth = 1.0
		context.strokeStyle = color
		context.strokeRect(x, y, w, h);
		context.restore()
	}

	private fun gradient(x: Number, y: Number, w: Number, h: Number, c1: String, c2: String) {
		context.save()
		var grd = context.createLinearGradient(x, y, w, h)!!
		grd.addColorStop(0, c1)
		grd.addColorStop(1, c2)
		context.fillStyle = grd
		//context.fill()
		context.fillRect(x, y, w, h);
		context.restore()
	}

	private fun stroke_rounded_rect(x: Int, y: Int, w: Int, h: Int, radius: Int, color: String) {
		context.save()
		context.strokeStyle = color;
		context.beginPath();
		// draw top and top right corner
		context.moveTo(x+radius,y);
		context.arcTo(x+w,y,x+w,y+radius,radius);

		// draw right side and bottom right corner
		context.arcTo(x+w,y+h,x+w-radius,y+h,radius);

		// draw bottom and bottom left corner
		context.arcTo(x,y+h,x,y+h-radius,radius);

		// draw left and top left corner
		context.arcTo(x,y,x+radius,y,radius);

		context.lineWidth = 1.0
		context.stroke()
		context.restore()
	}

	private fun drawButtonRect(x: Int, y: Int, w: Int, h: Int, variant: Variant, disabled: Boolean, hover: Boolean, down: Boolean) {
		val radius = 2

		context.save()
		context.beginPath();
		context.strokeStyle = "black";
		context.lineWidth = 1.0

		// draw top and top right corner
		context.moveTo(x+radius,y);
		context.arcTo(x+w,y,x+w,y+radius,radius);

		// draw right side and bottom right corner
		context.arcTo(x+w,y+h,x+w-radius,y+h,radius);

		// draw bottom and bottom left corner
		context.arcTo(x,y+h,x,y+h-radius,radius);

		// draw left and top left corner
		context.arcTo(x,y,x+radius,y,radius);

		val (bottomColor, topColor) = when (variant) {
			Variant.INFO -> Pair("#1D5388", "#6290BC")
			Variant.SUCCESS -> Pair("#5D7A1F", "#C4FF44")
			Variant.WARNING -> Pair("#FBB900", "#FBF000")
			Variant.DANGER -> Pair("#FF4300", "#FFB096")
			Variant.DEFAULT -> Pair("#3B3B3B", "#535353")
		}
		context.fillStyle = if (disabled){
			"#434343"
		} else if (down){
			bottomColor
		} else if (hover) {
			topColor
		} else {
			var grd = context.createLinearGradient(x + w / 2, y + h, x + w / 2, y)!!
			grd.addColorStop(0, bottomColor)
			grd.addColorStop(1, topColor)
			grd
		}
		context.fill()
		context.stroke()
		context.closePath();

		context.restore()
	}

	private fun fillStrokeRoundedRect(x: Int, y: Int, w: Int, h: Int, fillColor: String, strokeColor: String) {
		val radius = 2

		context.save()
		context.beginPath();
		context.lineWidth = 1.0

		// draw top and top right corner
		context.moveTo(x+radius,y);
		context.arcTo(x+w,y,x+w,y+radius,radius);

		// draw right side and bottom right corner
		context.arcTo(x+w,y+h,x+w-radius,y+h,radius);

		// draw bottom and bottom left corner
		context.arcTo(x,y+h,x,y+h-radius,radius);

		// draw left and top left corner
		context.arcTo(x,y,x+radius,y,radius);

		context.fillStyle = fillColor
		context.strokeStyle = strokeColor

		context.fill()
		context.stroke()
		context.closePath();

		context.restore()
	}

	private fun fill_rounded_rect(x: Int, y: Int, w: Int, h: Int, radius: Int, color: String) {
		context.save()
		context.fillStyle = color;
		context.beginPath();
		// draw top and top right corner
		context.moveTo(x+radius,y);
		context.arcTo(x+w,y,x+w,y+radius,radius);

		// draw right side and bottom right corner
		context.arcTo(x+w,y+h,x+w-radius,y+h,radius);

		// draw bottom and bottom left corner
		context.arcTo(x,y+h,x,y+h-radius,radius);

		// draw left and top left corner
		context.arcTo(x,y,x+radius,y,radius);

		context.fill()
		context.restore()
	}

	private fun text(text: String, x: Number, y: Number, color: String, font: Font) {
		context.fillStyle = color;
		context.font = font.toString()
		context.textBaseline = "top"
		context.fillText(text, x, y)
	}
}