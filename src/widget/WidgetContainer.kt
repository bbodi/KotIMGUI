package widget

abstract class WidgetContainer(widgetHandler: WidgetHandler, init: WidgetContainer.() -> Unit) : Widget(widgetHandler) {
	val widgets = arrayListOf<Widget>();
	{
		this.init()
	}

	fun Widget.plus() {
		widgets.add(this)
	}
}