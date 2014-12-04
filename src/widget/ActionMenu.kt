package widget

import skin.Variant
import timeline.BooleanValue
import timeline.app
import timeline.context

class ActionItem(pos: Pos, init: ActionItem.() -> Unit) : Widget(pos) {
	var label = ""
	var disabled = false
	var checkBoxValue: BooleanValue? = null
	var onClick: (() -> Unit)? = null
	var onHover: (() -> Unit)? = null
	var onHoverOut: (() -> Unit)? = null
	var variant = Variant.DEFAULT
	var hasSubMenu = false
	var comment: String? = null

	override var height: Int = app.skin.rowHeight
		private set

	val hover: Boolean
		get() = app.mousePos.is_in_rect(pos, Pos(parent!!.width, height));

	var highlight: Boolean = false
		get() = hover || $highlight


	{
		init()
	}

	override var width: Int = label.length() * app.skin.charWidth
		private set

	override fun draw() {
		app.skin.drawActionItem(this)
	}

	override fun handleEvents() {
		val was_hot = app.hot_widget_id == id
		if (hover && !was_hot) {
			app.hot_widget_id = id
			if (onHover != null) {
				onHover!!()
			}
		} else if (was_hot && !hover) {
			app.hot_widget_id = null
			if (onHoverOut != null) {
				onHoverOut!!()
			}
		}

		val clicked = app.leftMouseButton.just_released && hover
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
	val hover = app.mousePos.is_in_rect(pos, Pos(width, height));

	{
		this.init()
	}

	override fun draw() {

	}

	override fun handleEvents() {
	}

}

class Separator(pos: Pos) : Widget(pos) {

	override fun draw() {

	}

	override fun handleEvents() {

	}
}

open class ActionMenu(pos: Pos, init: ActionMenu.() -> Unit) : WidgetContainer(pos) {
	var variant = Variant.DEFAULT
	var visible: BooleanValue = BooleanValue(true);

	var hover = false
		private set
		get() {
			return app.mousePos.is_in_rect(pos, Pos(width, height))
		}

	override fun handleEvents() {
		if (!visible.value) {
			return
		}
		widgets.forEach { it.handleEvents() }
	}

	{
		init()
		val (w, h) = calcContentSize()
		if (this.width == 0) {
			this.width = w + marginX
		}
		if (this.height == 0) {
			this.height = h + marginY
		}
	}

	override fun draw() {
		/*if (!visible.value) {
			return
		}
		val x = pos.x
		val y = pos.y
		val w = width
		val h = height
		app.skin.drawPanelRect(x, y, w, h, variant)
		context.save()
		context.rect(contentX, contentY, contentWidth, contentHeight)
		context.clip()
		widgets.forEach { it.draw() }
		context.restore()*/
		app.skin.drawActionMenu(this)
		widgets.forEach { it.draw() }
	}
}