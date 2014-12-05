package timeline

import timeline.Application
import skin.DiscoverUI
import widget.Pos

private val app: Application = object : Application(DiscoverUI(1397, 796, 3)) {
	val eventTemplates: MutableList<EventTemplate> = arrayListOf()
	val eventTemplatePanel = EventTemplatePanel(appState.metrics)
	override fun doFrame() {
		doAppLogic()
	}
	private fun doAppLogic() {
		eventTemplatePanel.drawEventTemplatePanel(Pos(100, 100), eventTemplates, appState, skin)
	}
}

fun main(args: Array<String>) {

}