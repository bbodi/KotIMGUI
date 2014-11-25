package widget

import skin.Variant
import timeline.BooleanValue

class ActionItem(widgetHandler: WidgetHandler, init: ActionItem.() -> Unit) : Widget(widgetHandler) {
	var label = ""
	var disabled = false
	var checkBoxValue: BooleanValue? = null
	var onClick: (() -> Unit)? = null
	var variant = Variant.DEFAULT
	var parent: ActionMenu? = null
	var hover = false
		private set

	{
		this.init()
	}

	override fun draw() {
		widgetHandler.skin.drawActionItem(this)
	}

	override var width: Int = 0
		get() = if (parent == null) label.length() * widgetHandler.skin.charWidth else parent!!.width
		private set

	override var height: Int = widgetHandler.skin.charHeight
		private set

	override fun calcOwnSize() {
		widgetHandler.skin.calcActionItemSize(this)
	}

	override fun handleEvents() {
		hover = widgetHandler.mouse_pos.is_in_rect(pos, AbsolutePos(width, height))
	}
}

class CheckboxItem(widgetHandler: WidgetHandler, init: CheckboxItem.() -> Unit) : Widget(widgetHandler) {
	var label = ""
	var disabled = false
	var checkBoxValue: BooleanValue? = null
	var onClick: (() -> Unit)? = null
	var variant = Variant.DEFAULT
	var hover = false
		private set

	{
		this.init()
		calcOwnSize()
	}

	override fun draw() {
		//widgetHandler.skin.drawActionItem(this)
	}

	override fun calcOwnSize() {
		//widgetHandler.skin.calcActionItemSize(this)
	}

	override fun handleEvents() {
		hover = widgetHandler.mouse_pos.is_in_rect(pos, AbsolutePos(width, height))
	}
}

class Separator(widgetHandler: WidgetHandler) : Widget(widgetHandler) {

	override fun draw() {

	}

	override fun calcOwnSize() {

	}

	override fun handleEvents() {

	}
}

open class ActionMenu(widget_handler: WidgetHandler, init: WidgetContainer.() -> Unit) : Panel(widget_handler, init) {

	override fun draw() {
		widget_handler.skin.drawActionMenu(this)
		widgets.forEach { it.draw() }
	}
}