package widget

import skin.Variant
import timeline.BooleanValue
import timeline.widgetHandler

class ActionItem(pos: Pos, init: ActionItem.() -> Unit) : Widget(pos) {
	var label = ""
	var disabled = false
	var checkBoxValue: BooleanValue? = null
	var onClick: (() -> Unit)? = null
	var onHover: (() -> Unit)? = null
	var onHoverOut: (() -> Unit)? = null
	var variant = Variant.DEFAULT
	var hasSubMenu = false

	override var height: Int = widgetHandler.skin.rowHeight
		private set

	{
		init()
	}

	override var width: Int = label.length() * widgetHandler.skin.charWidth
		private set

	val hover: Boolean
		get()= widgetHandler.mousePos.is_in_rect(pos, AbsolutePos(parent!!.width, height))

	override val id = PositionBasedId(pos.x, pos.y, label.hashCode()).hashCode()

	override fun draw() {
		widgetHandler.skin.drawActionItem(this)
	}

	override fun handleEvents() {
		val was_hot = widgetHandler.hot_widget_id == id
		if (hover && !was_hot) {
			widgetHandler.hot_widget_id = id
			if (onHover != null) {
				onHover!!()
			}
		} else if (was_hot && !hover) {
			widgetHandler.hot_widget_id = null
			if (onHoverOut != null) {
				onHoverOut!!()
			}
		}

		val clicked = widgetHandler.leftMouseButton.just_released && hover
		if (clicked && onClick != null) {
			onClick!!()
		}
	}
}

class CheckboxItem(val checkBoxValue: BooleanValue, pos: Pos, init: CheckboxItem.() -> Unit) : Widget(pos) {
	var label = ""
	var disabled = false
	var onClick: (() -> Unit)? = null
	var variant = Variant.DEFAULT
	val hover = widgetHandler.mousePos.is_in_rect(pos, AbsolutePos(width, height))
	override var id = checkBoxValue.hashCode();

	{
		this.init()
	}

	override fun draw() {

	}

	override fun handleEvents() {
	}

}

class Separator(pos: Pos) : Widget(pos) {
	override val id: Int = 0

	override fun draw() {

	}

	override fun handleEvents() {

	}
}

open class ActionMenu(pos: Pos, init: Panel.() -> Unit) : Panel(pos, init) {
	override val id: Int = 0

	override fun draw() {
		widgetHandler.skin.drawActionMenu(this)
		widgets.forEach { it.draw() }
	}
}