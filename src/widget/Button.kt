package widget

import skin.Variant
import timeline.app

class Button(val label: String, pos: Pos, init: Button.() -> Unit) : Widget(pos) {
	var disabled: Boolean = false
	val hover: Boolean
		get() = app.mousePos.is_in_rect(pos, Pos(width, height))

	override var width = 0
		get() = if ($width == 0) (label.length+2) * app.skin.charWidth else $width

	override var height = app.skin.rowHeight
	var down = false
	var variant = Variant.DEFAULT
	var onClick: (() -> Unit)? = null
	{
		init()
	}

	override fun draw() {
		app.skin.drawButton(this)
	}

	override fun handleEvents() {
		val was_hot = app.hot_widget_id == id
		val was_active = app.active_widget_id == id
		down = was_active && !app.leftMouseButton.just_released;

		if (app.leftMouseButton.down && hover && !was_active) {
			app.active_widget_id = id
		} else if (was_active && app.leftMouseButton.just_released) {
			app.active_widget_id = null
		}

		if (hover && !was_hot) {
			app.hot_widget_id = id
		} else if (was_hot && !hover) {
			app.hot_widget_id = null
		}
		if (clicked && onClick != null) {
			onClick!!()
		}
	}

	val clicked: Boolean
		get() = !disabled && app.leftMouseButton.just_released && hover
}