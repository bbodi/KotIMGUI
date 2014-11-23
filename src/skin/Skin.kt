package skin

import widget.Button
import widget.WidgetHandler
import widget.HScrollBar
import widget.VScrollBar
import widget.Panel
import widget.Textfield


trait Skin {
	val charHeight: Int

	fun drawButton(widgetHandler: WidgetHandler, widget: Button)
	fun drawHorizontalScrollbar(widgetHandler: WidgetHandler, widget: HScrollBar)
	fun drawVerticalScrollbar(widgetHandler: WidgetHandler, widget: VScrollBar)
	fun drawPanel(widgetHandler: WidgetHandler, widget: Panel)

	fun drawTextfield(widgetHandler: WidgetHandler, widget: Textfield)
}

public enum class Variant {
	DEFAULT
	SUCCESS
	WARNING
	DANGER
	DARK
}