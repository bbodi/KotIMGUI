package widget

import skin.Variant
import timeline.widgetHandler

class Button(val label: String, pos: Pos, init: Button.() -> Unit) : Widget(pos) {
	var disabled: Boolean = false
	var allow_multi_click = false
	var hover = false
		private set
		get() = widgetHandler.mousePos.is_in_rect(pos, AbsolutePos(width, height))

	override var width = 0
		get() {
			return if ($width == 0) (label.length+2) * widgetHandler.skin.charWidth else $width
		}
	override var height = widgetHandler.skin.rowHeight
		private set
	var down = false
	var variant = Variant.DEFAULT
	var onClick: (() -> Unit)? = null
	{
		init()
	}
	override val id: Int = PositionBasedId(this.pos.x, this.pos.y, label.hashCode()).hashCode()

	override fun draw() {
		widgetHandler.skin.drawButton(this)
	}

	override fun handleEvents() {
		val was_hot = widgetHandler.hot_widget_id == id
		val was_active = widgetHandler.active_widget_id == id
		down = was_active && !widgetHandler.leftMouseButton.just_released;

		if (widgetHandler.leftMouseButton.down && hover && !was_active) {
			widgetHandler.active_widget_id = id
		} else if (was_active && widgetHandler.leftMouseButton.just_released) {
			widgetHandler.active_widget_id = null
		}

		if (hover && !was_hot) {
			widgetHandler.hot_widget_id = id
		} else if (was_hot && !hover) {
			widgetHandler.hot_widget_id = null
		}
		if (clicked && onClick != null) {
			onClick!!()
		}
	}

	val clicked: Boolean
		get() = !disabled && widgetHandler.leftMouseButton.just_released && hover
}