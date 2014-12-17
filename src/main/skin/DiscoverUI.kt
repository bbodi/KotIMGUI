package skin

import widget.Button
import widget.HScrollBar
import widget.VScrollBar
import widget.Panel
import widget.Textfield
import timeline.setCursor
import timeline.CursorStyle
import widget.ActionItem
import widget.ActionMenu
import widget.Pos
import widget.Checkbox
import widget.RadioButton
import widget.TabPanel
import widget.Label
import timeline.Application
import timeline.AppSizeMetricData
import kotlin.js.dom.html.HTMLElement
import kotlin.js.dom.html.window
import kotlin.js.dom.html5.HTMLCanvasElement
import kotlin.js.dom.html5.CanvasContext
import widget.Widget
import timeline.debugLines

public class DiscoverUI(val appCanvasContext: CanvasContext, val width: Int, val height: Int, val rowHeightPercent: Int) : Skin {

	private val OUTER_PANEL_COLOR: String = "#434343"
	private val appSizeMetricData: AppSizeMetricData
	private val canvasCache = hashMapOf<String, HTMLCanvasElement>();

	{
		val rowHeight = (height * (rowHeightPercent / 100.0) + 0.5).toInt()
		val panelBorder = 5
		val textMarginY = rowHeight / 4
		val charHeight = rowHeight - textMarginY * 2
		val font = Font(charHeight, "Courier New");
		appCanvasContext.font = font.toString()
		val charWidth = (appCanvasContext.measureText("M")!!.width + 0.5).toInt()
		appSizeMetricData = AppSizeMetricData(font, rowHeight, textMarginY, charWidth, charHeight, panelBorder)
	}

	override fun getAppSizeMetricData(): AppSizeMetricData {
		return appSizeMetricData
	}

	override fun clear() {
		debugLines.add("cacheSize: ${canvasCache.size}")
		appCanvasContext.fillStyle = "#2C2C2C"
		appCanvasContext.fillRect(0, 0, width, height)
		appCanvasContext.strokeStyle = "#000000"
		appCanvasContext.lineWidth = 4.0
		appCanvasContext.strokeRect(0, 0, width, height)
	}

	fun getTextfieldStrokeColor(variant: Variant): ColorStates {
		return when (variant) {
			Variant.INFO -> ColorStates("#29A1D3", "#58B4DB", "#2B7B9E")
			Variant.DEFAULT -> ColorStates("#525864", "#6B6B6B", "#2B2525")
			Variant.SUCCESS -> ColorStates("#8AB71C", "#9BBC45", "#637C1D")
			Variant.WARNING -> ColorStates("#F1B018", "#F7C44C", "#B78A21")
			Variant.DANGER -> ColorStates("#EE4E10", "#F47344", "#B5441B")
		}
	}

	override fun drawCheckbox(checkbox: Checkbox) {
		val id = with(checkbox, {"Checkbox: ${width},${height},${variant},${label},${disabled},${value.value}"})
		val cachedCanvas = cache(id, checkbox.width, checkbox.height, { context ->
			drawButtonRect(context, 0, appSizeMetricData.textMarginY, appSizeMetricData.charHeight, appSizeMetricData.charHeight, checkbox.variant, checkbox.disabled, checkbox.hover, false)
			if (checkbox.value.value) {
				context.save()
				context.beginPath();
				context.strokeStyle = "black";
				context.lineWidth = 4.0

				// draw top and top right corner
				context.moveTo(3, appSizeMetricData.charHeight / 2)
				context.lineTo(appSizeMetricData.charHeight / 2 - 3, appSizeMetricData.charHeight - 3)
				context.lineTo(appSizeMetricData.charHeight - 3, 3)
				context.closePath()
				context.stroke()
				context.restore()
			}
			val textX = appSizeMetricData.charHeight + appSizeMetricData.charWidth / 2
			val textY = appSizeMetricData.textMarginY
			renderText(context, checkbox.label, textX, textY, if (checkbox.disabled) "#8C8C8C" else "white", appSizeMetricData.font)
		})
		val x = checkbox.pos.x
		val y = checkbox.pos.y
		appCanvasContext.drawImage(cachedCanvas, x, y)
	}

	override fun drawRadioButton(radioButton: RadioButton) {
		val id = with(radioButton, {"RadioButton: ${width},${height},${variant},${label},${disabled},${value.value == order}"})
		val cachedCanvas = cache(id, radioButton.width, radioButton.height, { context ->
			val radius = appSizeMetricData.charHeight / 2
			val circleX = radius
			val circleY = appSizeMetricData.rowHeight / 2
			drawCircle(context, circleX, circleY, radius, radioButton.variant, radioButton.disabled, radioButton.hover)
			if (radioButton.value.value == radioButton.order) {
				fillCircle(context, circleX, circleY, appSizeMetricData.charHeight / 4, "black")
			}
			val textX = circleX + radius + appSizeMetricData.charWidth / 2
			val textY = appSizeMetricData.textMarginY
			renderText(context, radioButton.label, textX, textY, if (radioButton.disabled) "#8C8C8C" else "white", appSizeMetricData.font)
		})
		val x = radioButton.pos.x
		val y = radioButton.pos.y
		appCanvasContext.drawImage(cachedCanvas, x, y)
	}

	override fun drawLabel(widget: Label) {
		val id = with(widget, {"Label: ${label}"})
		val cachedCanvas = cache(id, widget.width, widget.height, { context ->
			val bottomColor = when (widget.variant) {
				Variant.INFO -> "#1D5388"
				Variant.SUCCESS -> "#5D7A1F"
				Variant.WARNING -> "#FBB900"
				Variant.DANGER -> "#FF4300"
				Variant.DEFAULT -> "white"
			}
			renderText(context, widget.label, 0, appSizeMetricData.textMarginY, bottomColor, appSizeMetricData.font)
		})
		appCanvasContext.drawImage(cachedCanvas, widget.pos.x, widget.pos.y)
	}

	override fun drawMiniButton(widget: Button) {
		val id = with(widget, {"MiniButton: ${width},${height},${variant},${label},${disabled},${hover},${down}"})
		val cachedCanvas = cache(id, widget.width, widget.height, { context ->
			val w = widget.width
			val h = widget.height

			drawButtonRect(context, 0, 0, w, h, widget.variant, widget.disabled, widget.hover, widget.down)
			drawMiniButtonText(context, widget.label, 0, 0, w, if (widget.disabled) "#8C8C8C" else "white")
		})

		if (widget.hover) {
			if (widget.disabled) {
				setCursor(CursorStyle.NotAllowed);
			} else {
				setCursor(CursorStyle.Pointer);
			}
		}
		val x = widget.pos.x
		val y = widget.pos.y
		appCanvasContext.drawImage(cachedCanvas, x, y)
	}

	private fun cache(id: String, width: Int, height: Int, renderFunc: (CanvasContext) -> Unit): HTMLCanvasElement {
		var cachedCanvas = canvasCache[id]
		if (cachedCanvas == null) {
			cachedCanvas = window.document.createElement("canvas") as HTMLCanvasElement
			cachedCanvas!!.width = width.toDouble()
			cachedCanvas!!.height = height.toDouble()
			canvasCache[id] = cachedCanvas!!
			renderFunc(cachedCanvas!!.getContext("2d")!!)
		}
		return cachedCanvas!!
	}

	override fun drawButton(widget: Button) {
		val id = with(widget, {"Button: ${width},${height},${variant},${label},${disabled},${hover},${down}"})
		val cachedCanvas = cache(id, widget.width, widget.height, { context ->
			val w = widget.width
			val h = widget.height
			drawButtonRect(context, 0, 0, w, h, widget.variant, widget.disabled, widget.hover, widget.down)
			drawButtonText(context, widget.label, 0, 0, w, if (widget.disabled) "#8C8C8C" else "white")
		})
		val x = widget.pos.x
		val y = widget.pos.y
		appCanvasContext.drawImage(cachedCanvas, x, y)
	}

	private fun drawButtonText(context: CanvasContext, label: String, x: Int, y: Int, w: Int, color: String) {
		val label_w = label.length * appSizeMetricData.charWidth
		val textX = x + w / 2 - label_w / 2
		val textY = y + appSizeMetricData.textMarginY
		renderText(context, label, textX, textY, color, appSizeMetricData.font)
	}

	public fun renderText(context: CanvasContext, text: String, x: Number, y: Number, color: String, font: Font) {
		context.fillStyle = color;
		context.font = font.toString()
		context.textBaseline = "hanging"
		context.fillText(text, x, y)
	}

	private fun drawMiniButtonText(context: CanvasContext, label: String, x: Int, y: Int, w: Int, color: String) {
		val label_w = label.length * appSizeMetricData.charWidth
		val textX = x + w / 2 - label_w / 2
		val textY = y + appSizeMetricData.textMarginY / 2
		renderText(context, label, textX, textY, color, appSizeMetricData.font)
	}

	override fun drawTextfield(widget: Textfield) {
		val id = with(widget, {"Textfield: ${width},${height},${variant},${textPtr.value},${cursorPos},${disabled},${isActive},${isCursorShown}"})
		val cachedCanvas = cache(id, widget.width, widget.height, { context ->
			val w = widget.width
			val h = widget.height
			fill_rounded_rect(context, 0, 0, w, h, 5, "#24252A")
			if (widget.variant != Variant.DEFAULT) {
				val (normalColor, hoverColor, activeColor) = getTextfieldStrokeColor(widget.variant)
				stroke_rounded_rect(context, 0, 0, w, h, 5, normalColor)
			} else if (widget.isActive) {
				val (normalColor, hoverColor, activeColor) = getTextfieldStrokeColor(Variant.INFO)
				stroke_rounded_rect(context, 0, 0, w, h, 5, normalColor)
			}

			val textX = appSizeMetricData.panelBorder
			val textY = appSizeMetricData.textMarginY
			renderText(context, widget.textPtr.value, textX, textY, "white", appSizeMetricData.font)
			if (widget.isCursorShown && widget.isActive) {
				renderText(context, "_", textX + appSizeMetricData.charWidth * widget.cursorPos, textY, "white", appSizeMetricData.font)
			}
		})
		val x = widget.pos.x
		val y = widget.pos.y
		appCanvasContext.drawImage(cachedCanvas, x, y)


		if (widget.hover) {
			if (widget.disabled) {
				setCursor(CursorStyle.NotAllowed);
			} else {
				setCursor(CursorStyle.Text);
			}
		}
	}

	override fun drawActionItem(actionItem: ActionItem) {
		val id = with(actionItem, {"ActionItem: ${width},${height},${variant},${label},${disabled},${highlight},${hasSubMenu}"})
		val cachedCanvas = cache(id, actionItem.width, actionItem.height, { context ->
			val w = actionItem.parent!!.width - 2 * appSizeMetricData.panelBorder
			val textX = appSizeMetricData.panelBorder
			val textY = appSizeMetricData.textMarginY
			val textColor = if (actionItem.disabled) {
				"#8C8C8C"
			} else {
				"white"
			}
			if (!actionItem.disabled && actionItem.highlight) {
				fillRect(context, 0, 0, w, actionItem.height, "#2C2C2C")
			}
			renderText(context, actionItem.label, textX, textY, textColor, appSizeMetricData.font)
			if (actionItem.hasSubMenu) {
				renderText(context, "▸", w - appSizeMetricData.charWidth, textY, textColor, appSizeMetricData.font)
			} else if (actionItem.comment != null) {
				renderText(context, actionItem.comment!!, w - appSizeMetricData.charWidth * (actionItem.comment!!.length + 1), textY, "#929292", appSizeMetricData.font)
			}
		})

		val x = actionItem.pos.x
		val y = actionItem.pos.y
		appCanvasContext.drawImage(cachedCanvas, x, y)
	}

	/*override fun drawHorizontalScrollbar(app: Application, widget: HScrollBar) {
		val x = widget.pos.x
		val y = widget.pos.y
		fill_rounded_rect(x, y+ appSizeMetricData.rowHeight /4, widget.width, appSizeMetricData.rowHeight /2, appSizeMetricData.rowHeight /4, "#24252A")
		val value = widget.value.value
		val value_range = widget.max_value - widget.min_value
		val value_percent = (value - widget.min_value) / value_range.toDouble()
		val orange_bar_w = widget.width * value_percent
		fill_rounded_rect(x, y+ appSizeMetricData.rowHeight /4, orange_bar_w.toInt(), appSizeMetricData.rowHeight /2, appSizeMetricData.rowHeight /4, "#EE4E10")

		context.save()
		context.beginPath();
		context.arc(x+orange_bar_w, y+ appSizeMetricData.rowHeight /2, appSizeMetricData.rowHeight /2, 0, 2 * Math.PI, false);
		context.fillStyle = "#F7F8F3"
		context.fill()

		context.restore()
	}*/

	/*override fun drawVerticalScrollbar(app: Application, widget: VScrollBar) {
		val x = widget.pos.x
		val y = widget.pos.y
		fill_rounded_rect(x + appSizeMetricData.charWidth/4, y, appSizeMetricData.charWidth/2, widget.height, appSizeMetricData.charWidth/4, "#24252A")
		val value = widget.value.value
		val value_range = widget.max_value - widget.min_value
		val value_percent = (value - widget.min_value) / value_range.toDouble()
		val orange_bar_h = widget.height * value_percent
		fill_rounded_rect(x+appSizeMetricData.charWidth/4, y, appSizeMetricData.charWidth/2, orange_bar_h.toInt(), appSizeMetricData.charWidth/4, "#EE4E10")

		context.save()
		context.beginPath();
		context.arc(x+appSizeMetricData.charWidth/2, y+orange_bar_h, appSizeMetricData.charWidth/2, 0, 2 * Math.PI, false);
		context.fillStyle = "#F7F8F3";
		context.fill();
		context.restore()
	}*/

	public override fun drawPanelRect(x: Int,
									  y: Int,
									  w: Int,
									  h: Int,
									  variant: Variant) {
		val id = "Panel: ${w},${h},${variant}"
		val cachedCanvas = cache(id, w, h, { context ->
			val (bottomColor, topColor) = when (variant) {
				Variant.INFO -> Pair("#1D5388", "#6290BC")
				Variant.SUCCESS -> Pair("#5D7A1F", "#C4FF44")
				Variant.WARNING -> Pair("#FBB900", "#FBF000")
				Variant.DANGER -> Pair("#FF4300", "#FFB096")
				Variant.DEFAULT -> Pair("#3B3B3B", "#535353")
			}
			fillRect(context, 0, 0, w, h, topColor) // outer bg OUTER_PANEL_COLOR
			strokeRect(context, 0, 0, w, h, "black") // outer line
			fillRect(context, 0 + appSizeMetricData.panelBorder, 0 + appSizeMetricData.panelBorder, w - appSizeMetricData.panelBorder * 2, h - appSizeMetricData.panelBorder * 2, bottomColor) // "#323232" inner bg
			strokeRect(context, 0 + appSizeMetricData.panelBorder, 0 + appSizeMetricData.panelBorder, w - appSizeMetricData.panelBorder * 2, h - appSizeMetricData.panelBorder * 2, "black") // outer line
		})
		appCanvasContext.drawImage(cachedCanvas, x, y)
	}

	override fun drawTabPanel(widget: TabPanel) {
		val id = with(widget, { "TabPanel: ${width},${height}" })
		val cachedCanvas = cache(id, widget.width, widget.height, { context ->
			val w = widget.width
			val h = widget.height
			var itemX = 0
			for ((i, item) in widget.items.withIndices()) {
				val down = i == widget.value.value
				if (down) {
					drawPanelRect(0, 0 + appSizeMetricData.rowHeight, w, h - appSizeMetricData.rowHeight, item.variant)
					drawSelectedTabPanelItem(context, itemX, 0, item.width, item.height, item.variant)
				} else {
					drawButtonRect(context, itemX, 0, item.width, item.height, item.variant, item.disabled, item.hover, down)
				}
				drawButtonText(context, item.label, itemX, 0, item.width, if (item.disabled) "#8C8C8C" else "white")
				itemX += item.width
			}
		})
		val x = widget.pos.x
		val y = widget.pos.y
		appCanvasContext.drawImage(cachedCanvas, x, y)
	}

	override fun drawActionMenu(actionMenu: ActionMenu) {
		val id = with(actionMenu, { "TabPanel: ${width},${height}" })
		val cachedCanvas = cache(id, actionMenu.width, actionMenu.height, { context ->
			fillStrokeRoundedRect(context, 0, 0, actionMenu.width, actionMenu.height, "#4A4A4A", "black")
		})
		val x = actionMenu.pos.x
		val y = actionMenu.pos.y
		appCanvasContext.drawImage(cachedCanvas, x, y)
	}

	private fun fillRect(context: CanvasContext, x: Number, y: Number, w: Number, h: Number, color: String) {
		context.save()
		context.fillStyle = color
		context.fillRect(x, y, w, h);
		context.restore()
	}

	private fun strokeRect(context: CanvasContext, x: Number, y: Number, w: Number, h: Number, color: String) {
		context.save()
		context.lineWidth = 1.0
		context.strokeStyle = color
		context.strokeRect(x, y, w, h);
		context.restore()
	}

	private fun gradient(context: CanvasContext, x: Number, y: Number, w: Number, h: Number, c1: String, c2: String) {
		context.save()
		var grd = context.createLinearGradient(x, y, w, h)!!
		grd.addColorStop(0, c1)
		grd.addColorStop(1, c2)
		context.fillStyle = grd
		//context.fill()
		context.fillRect(x, y, w, h);
		context.restore()
	}

	private fun stroke_rounded_rect(context: CanvasContext, x: Int, y: Int, w: Int, h: Int, radius: Int, color: String) {
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

	private fun drawButtonRect(context: CanvasContext, x: Int, y: Int, w: Int, h: Int, variant: Variant, disabled: Boolean, hover: Boolean, down: Boolean) {
		val radius = 2

		context.save()
		context.beginPath();
		context.strokeStyle = "black";
		context.lineWidth = 1.0

		// draw top and top right corner
		context.moveTo(x + radius, y);
		context.arcTo(x + w, y, x + w, y + radius, radius);

		// draw right side and bottom right corner
		context.arcTo(x + w, y + h, x + w - radius, y + h, radius);

		// draw bottom and bottom left corner
		context.arcTo(x, y + h, x, y + h - radius, radius);

		// draw left and top left corner
		context.arcTo(x, y, x + radius, y, radius);

		val (bottomColor, topColor) = when (variant) {
			Variant.INFO -> Pair("#1D5388", "#6290BC")
			Variant.SUCCESS -> Pair("#5D7A1F", "#C4FF44")
			Variant.WARNING -> Pair("#FBB900", "#FBF000")
			Variant.DANGER -> Pair("#FF4300", "#FFB096")
			Variant.DEFAULT -> Pair("#3B3B3B", "#535353")
		}
		context.fillStyle = if (disabled) {
			"#434343"
		} else if (down) {
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

		context.restore()
	}

	private fun drawSelectedTabPanelItem(context: CanvasContext, x: Int, y: Int, w: Int, h: Int, variant: Variant) {
		val (bottomColor, topColor) = when (variant) {
			Variant.INFO -> Pair("#1D5388", "#6290BC")
			Variant.SUCCESS -> Pair("#5D7A1F", "#C4FF44")
			Variant.WARNING -> Pair("#FBB900", "#FBF000")
			Variant.DANGER -> Pair("#FF4300", "#FFB096")
			Variant.DEFAULT -> Pair("#3B3B3B", "#535353")
		}

		var grd = context.createLinearGradient(x + w / 2, y + h, x + w / 2, y)!!
		grd.addColorStop(0, topColor)
		grd.addColorStop(1, bottomColor)
		context.fillStyle = grd
		context.save()
		context.fillRect(x, y, w, h + 4)
		context.restore()
	}

	private fun fillCircle(context: CanvasContext, x: Int, y: Int, r: Int, color: String) {
		context.save()
		context.beginPath()
		context.arc(x, y, r, 0, 2 * Math.PI, false)

		context.fillStyle = color
		context.fill()
		context.restore()
	}

	private fun drawCircle(context: CanvasContext, x: Int, y: Int, r: Int, variant: Variant, disabled: Boolean, hover: Boolean) {
		val (bottomColor, topColor) = when (variant) {
			Variant.INFO -> Pair("#1D5388", "#6290BC")
			Variant.SUCCESS -> Pair("#5D7A1F", "#C4FF44")
			Variant.WARNING -> Pair("#FBB900", "#FBF000")
			Variant.DANGER -> Pair("#FF4300", "#FFB096")
			Variant.DEFAULT -> Pair("#3B3B3B", "#535353")
		}
		val color = if (disabled) {
			"#434343"
		} else if (hover) {
			topColor
		} else {
			bottomColor
		}
		context.save()
		context.beginPath()
		context.lineWidth = 1.0
		context.fillStyle = color
		context.strokeStyle = "black"
		context.arc(x, y, r, 0, 2 * Math.PI, false)
		context.fill()
		context.stroke()
		context.restore()
	}

	private fun fillStrokeRoundedRect(context: CanvasContext, x: Int, y: Int, w: Int, h: Int, fillColor: String, strokeColor: String) {
		val radius = 2

		context.save()
		context.beginPath();
		context.lineWidth = 1.0

		// draw top and top right corner
		context.moveTo(x + radius, y);
		context.arcTo(x + w, y, x + w, y + radius, radius);

		// draw right side and bottom right corner
		context.arcTo(x + w, y + h, x + w - radius, y + h, radius);

		// draw bottom and bottom left corner
		context.arcTo(x, y + h, x, y + h - radius, radius);

		// draw left and top left corner
		context.arcTo(x, y, x + radius, y, radius);
		context.fillStyle = fillColor
		context.strokeStyle = strokeColor

		context.fill()
		context.stroke()
		context.restore()
	}

	private fun fill_rounded_rect(context: CanvasContext, x: Int, y: Int, w: Int, h: Int, radius: Int, color: String) {
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
}