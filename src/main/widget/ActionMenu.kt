package widget

import skin.Variant
import timeline.BooleanValue
import timeline.context
import timeline.AppSizeMetricData
import timeline.AppState
import skin.Skin

class ActionItem(val label: String, pos: Pos, metrics: AppSizeMetricData, init: ActionItem.() -> Unit = {}) : Widget(pos) {
	var disabled = false
	var checkBoxValue: BooleanValue? = null
	var onClick: (() -> Unit)? = null
	var onHover: (() -> Unit)? = null
	var onHoverOut: (() -> Unit)? = null
	var variant = Variant.DEFAULT
	var hasSubMenu = false
	var comment: String? = null

	override var height: Int = metrics.rowHeight
		private set

	var hover = false

	var highlight: Boolean = false
		get() = hover || $highlight


	{
		init()
	}

	override var width: Int = label.length() * metrics.charWidth
		private set

	override fun draw(skin: Skin) {
		skin.drawActionItem(this)
	}

	override fun handleEvents(state: AppState) {
		hover = state.mousePos.isInRect(pos, Pos(width, height))
		val was_hot = state.hot_widget_id == id
		if (hover && !was_hot) {
			state.hot_widget_id = id
			if (onHover != null) {
				onHover!!()
			}
		} else if (was_hot && !hover) {
			state.hot_widget_id = null
			if (onHoverOut != null) {
				onHoverOut!!()
			}
		}

		val clicked = state.leftMouseButton.just_released && hover
		if (clicked && onClick != null) {
			onClick!!()
		}
	}
}

class CheckboxItem(val checkBoxValue: BooleanValue, pos: Pos, metrics: AppSizeMetricData, init: CheckboxItem.() -> Unit) : Widget(pos) {
	var label = ""
	var disabled = false
	var onClick: (() -> Unit)? = null
	var variant = Variant.DEFAULT
	var hover = false

	{
		this.init()
	}

	override fun draw(skin: Skin) {

	}

	override fun handleEvents(state: AppState) {
		hover = state.mousePos.isInRect(pos, Pos(width, height))
	}

}


open class ActionMenu(pos: Pos, metrics: AppSizeMetricData, init: ActionMenu.() -> Unit) : WidgetContainer(pos, metrics) {
	var variant = Variant.DEFAULT
	var visible: BooleanValue = BooleanValue(true);

	var hover = false

	override fun handleEvents(state: AppState) {
		if (!visible.value) {
			return
		}
		hover = state.mousePos.isInRect(pos, Pos(width, height))
		widgets.forEach { it.handleEvents(state) }
	}

	{
		init()
		val (w, h) = calcContentSize()
		if (this.width == 0) {
			this.width = w + metrics.panelBorder
		}
		if (this.height == 0) {
			this.height = h + metrics.panelBorder
		}
	}

	override fun draw(skin: Skin) {
		/*if (!visible.value) {
			return
		}
		val x = pos.x
		val y = pos.y
		val w = width
		val h = height
		metrics.drawPanelRect(x, y, w, h, variant)
		context.save()
		context.rect(contentX, contentY, contentWidth, contentHeight)
		context.clip()
		widgets.forEach { it.draw() }
		context.restore()*/
		skin.drawActionMenu(this)
		widgets.forEach { it.draw(skin) }
	}
}