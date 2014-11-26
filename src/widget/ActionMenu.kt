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
	override var width: Int = 0
		get() = if (parent == null) label.length() * widgetHandler.skin.charWidth else parent!!.width
		private set

	override var height: Int = widgetHandler.skin.charHeight
		private set

	{
		this.init()
	}

	val hover = widgetHandler.mouse_pos.is_in_rect(pos, AbsolutePos(width, height));

	override var id = PositionBasedId(pos.x, pos.y, label.hashCode()).hashCode();

	override fun draw() {
		widgetHandler.skin.drawActionItem(this)
	}

	override fun handleEvents() {

	}
}

class CheckboxItem(widgetHandler: WidgetHandler, val checkBoxValue: BooleanValue, init: CheckboxItem.() -> Unit) : Widget(widgetHandler) {
	var label = ""
	var disabled = false
	var onClick: (() -> Unit)? = null
	var variant = Variant.DEFAULT
	val hover = widgetHandler.mouse_pos.is_in_rect(pos, AbsolutePos(width, height))
	override var id = checkBoxValue.hashCode();

	{
		this.init()
	}

	override fun draw() {

	}

	override fun handleEvents() {
	}

}

class Separator(widgetHandler: WidgetHandler) : Widget(widgetHandler) {
	override val id: Int = 0

	override fun draw() {

	}

	override fun handleEvents() {

	}
}

open class ActionMenu(widget_handler: WidgetHandler, init: Panel.() -> Unit) : Panel(widget_handler, init) {
	override val id: Int = 0

	override fun draw() {
		widget_handler.skin.drawActionMenu(this)
		widgets.forEach { it.draw() }
	}
}