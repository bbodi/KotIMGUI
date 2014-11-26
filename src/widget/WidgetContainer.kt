package widget

abstract class WidgetContainer(widgetHandler: WidgetHandler) : Widget(widgetHandler) {
	val widgets = arrayListOf<Widget>();

	fun Widget.plus() {
		widgets.add(this)
	}

	open fun addWidget(widget: Widget) {
		widgets.add(widget)
	}
}