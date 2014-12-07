package timeline

import timeline.Application
import skin.DiscoverUI
import widget.Pos
import widget.chart.FloatsAndSinkersChart
import widget.chart.LineChart
import skin.Variant
import widget.Panel
import widget.Button
import html5.localstorage.localStorage
import timeline.json
import kotlin.js
import widget.TabPanel
import widget.WidgetContainer
import widget.Label

private val app: Application = object : Application(DiscoverUI(1397, 796, 3)) {
	val eventTemplates: MutableList<EventTemplate> = arrayListOf()
	val eventTemplatePanel = EventTemplatePanel(appState.metrics)
	var unsaved = false
	val tabIndex = IntValue(0);
	val events = arrayListOf<Event>()
	val openTabs = arrayListOf<EventTemplate>()

	;
	{
		loadData()
	}


	override fun doFrame() {
		doAppLogic()
		timeline()
	}

	private fun doAppLogic() {
		Button("Save", Pos(0, 0), appState.metrics, {
			width = 200
			variant = if (unsaved) Variant.DANGER else Variant.DEFAULT
			onClick = {
				persist()
				unsaved = false
			}
		}).drawAndHandleEvents(appState, skin)
		TabPanel(tabIndex, Pos(100, 100), appState.metrics, {
			addTabPanelItem("Sablonok")
			addTabPanelItem("Bejegyzések")
			addTabPanelItem("Ma")
			openTabs.forEach {
				addTabPanelItem(it.name.value, {
					variant = Variant.INFO
				})
			}
			val panel: WidgetContainer = this
			if (tabIndex.value == 0) {
				val changed = eventTemplatePanel.drawEventTemplatePanel(eventTemplates, appState, panel)
				unsaved = unsaved || changed
			} else if (tabIndex.value == 1) {
				eventTemplates.forEach { template ->
					+Label(template.name.value, downAlongLeftMargin(), metrics)
					+Button("Megnyitás új tabfülön", toRightFromLastWidget(10), metrics, {
						onClick = {
							openTabs.add(template)
						}
					})
				}
			} else if (tabIndex.value > 2) {
				val template = openTabs[tabIndex.value - 3]
			}
		}).drawAndHandleEvents(appState, skin)
	}

	private fun loadData() {
		val jsonString = getLocalStorageItem("TimelineApp.eventTemplates") as String?
		if (jsonString == null) {
			return
		}
		val json = JsonParser(jsonString).parse()
		val eventTemplates = json["templates"]
		if (eventTemplates == null) {
			return
		}
		for (templateJson in eventTemplates.getArray()) {
			val template = 	templateJson["template"]!!
			val name = template["name"]!!.getString()
			val type = template["type"]!!.getString()
			this.eventTemplates.add(EventTemplate(name, EventType.valueOf(type)))
		}
	}

	private fun persist() {
		val templatesArray = json {
			"templates"..eventTemplates.map {
				json {
					"template"..{
						"name"..it.name.value
						"type"..it.type.toString()
					}
				}
			}.copyToArray()
		}
		val dto = templatesArray.toString()
		setLocalStorageItem("TimelineApp.eventTemplates", dto)
	}

	private fun timeline() {
		Panel(Pos(700, 100), appState.metrics, {
			+Timeline(downAlongLeftMargin(), 500, 500, appState.metrics, {
				for ( (i, v) in booleanValues.zip(showAvgValues).withIndices()) {
					val (showGraph, showAvg) = v
					if (i == 0 && showGraph.value) {
						+FloatsAndSinkersChart(graphData[i], graphAvgData[i], {
							actualDataColor = "#29A1D3"
							trendColor = "#EE4E10"
						})
					} else if (i == 1 && showGraph.value) {
						+FloatsAndSinkersChart(graphData[i], graphAvgData[i], {
							actualDataColor = "#29A1D3"
							trendColor = "#EE4E10"
						})
					} else if (showGraph.value) {
						+LineChart(graphData[i], {
							color = when (Variant.values()[i]) {
								Variant.INFO -> "#29A1D3"
								Variant.DEFAULT -> "#525864"
								Variant.SUCCESS -> "#8AB71C"
								Variant.WARNING -> "#F1B018"
								Variant.DANGER -> "#EE4E10"
							}
						})
						if (showAvg.value) {
							+LineChart(graphAvgData[i], {
								color = when (Variant.values()[i]) {
									Variant.DANGER -> "#58B4DB"
									Variant.WARNING -> "#6B6B6B"
									Variant.INFO -> "#9BBC45"
									Variant.DEFAULT -> "#F7C44C"
									Variant.SUCCESS -> "#F47344"
								}
							})
						}
					}
				}
			})
		}).drawAndHandleEvents(appState, skin)
	}
}

fun main(args: Array<String>) {

}

val graphData = array(initBali(), initMaki(), init_data(), init_data(), init_data())
val graphAvgData = array(calc_ema(graphData[0], 0.9f), calc_ema(graphData[1], 0.9f), calc_ema(graphData[2], 0.9f), calc_ema(graphData[3], 0.9f), calc_ema(graphData[4], 0.9f))
val bestFitLines = array<List<Float>>(arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())

fun initBali(): MutableList<Float?> = arrayListOf<Float?>(80f, 79.5f, 79.0f, 78.4f, 78.3f, 78.5f, 78.6f, 78.4f,
		78.6f, 78.1f, null, 77.9f, 78f, 78f, null, 78.5f, 78.1f, 78f, 77.9f, 77.4f, 77.6f, 77.7f)

fun initMaki(): MutableList<Float?> = arrayListOf<Float?>(55f, 55.1f, 54.8f, 54.5f, 54.3f, 54.4f, null, 54.8f,
		54.9f, 54.5f, 53.9f, 53.8f, 53.6f, 53.7f, null, 54.7f, 54.4f, 54.0f, 53.9f, 53.8f, 53.5f, 53.7f)