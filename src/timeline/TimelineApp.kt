package timeline

import timeline.Application
import skin.DiscoverUI
import widget.Pos

public val app: Application = object : Application(DiscoverUI(1397, 796, 3)) {
	val eventTemplates: MutableList<EventTemplate> = arrayListOf()
	val eventTemplatePanel = EventTemplatePanel()
	override fun doFrame() {
		doAppLogic(this)
	}
	private fun doAppLogic(app: Application) {
		eventTemplatePanel.drawEventTemplatePanel(Pos(100, 100), eventTemplates)
	}
}

fun main(args: Array<String>) {

}