package widget

import skin.Variant
import timeline.BooleanValue

class ActionItem(widgetHandler: WidgetHandler, init: ActionItem.() -> Unit) : Widget(widgetHandler) {
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
		widgetHandler.skin.drawActionItem(this)
	}

	override fun calcOwnSize() {
		widgetHandler.skin.calcActionItemSize(this)
	}

	override fun handleEvents() {

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

class ActionMenu(widgetHandler: WidgetHandler, init: WidgetContainer.() -> Unit) : Panel(widgetHandler, init) {

}