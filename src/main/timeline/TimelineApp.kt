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
import java.util.ArrayList
import java.util.HashMap
import widget.Checkbox

private class TimelineApp : Application(DiscoverUI(context, 1397, 796, 3)) {
	val eventTemplates: MutableList<EventTemplate> = arrayListOf()
	val eventItems: MutableList<EventItem> = arrayListOf()
	val eventTemplatePanel = EventTemplateEditorPanel(appState.metrics)
	var unsaved = false
	val tabIndex = Ptr(0);
	val events = arrayListOf<EventItem>()
	val openTabs = arrayListOf<EventTemplate>()


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
		}).drawAndHandleEvents(appState, context, skin)
		TabPanel(tabIndex, Pos(100, 20), appState.metrics, {
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
				val year = Date().getYear()
				val month = Date().getMonth()
				for (day in 0..30) {
					val label = Label("$year.${month+1}.${day+1}", downAlongLeftMargin(), metrics)
					+label
					val currentDate = newDate(year, month, day)
					val absoluteDay = currentDate.getTime() / 1000 / 60 / 60 / 24
					val dataPtr = baliTomeg[absoluteDay]
					+Checkbox("null", Ptr(dataPtr == null), toRightFromLastWidget(10), metrics, {
						onChange = { checked ->
							if (checked) {
								baliTomeg.remove(absoluteDay)
							} else {
								baliTomeg[absoluteDay] = Ptr(0f)
							}
						}
					})
					if (dataPtr != null) {
						var item = eventItems.filter { it.date.getMonth() == month && it.date.getYear() == year && it.date.getDayOfMonth() == day && it.type == template.typePtr.value }.singleOrNull()
						if (item == null) {
							item = EventItemFactory.getEventItem(template.typePtr.value, newDate(year, month, day), dataPtr)
							eventItems.add(item!!)
						}
						item!!.getListingWidgets(panel, appState)
					}
				}
			}
		}).drawAndHandleEvents(appState, context, skin)
	}

	fun loadData() {
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
						"type"..it.typePtr.toString()
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
						+FloatsAndSinkersChart(baliTomeg, calc_ema(baliTomeg, 0.9f), {
							actualDataColor = "#29A1D3"
							trendColor = "#EE4E10"
						})
					} else if (i == 1 && showGraph.value) {
						+FloatsAndSinkersChart(baliTomeg, calc_ema(baliTomeg, 0.9f), {
							actualDataColor = "#29A1D3"
							trendColor = "#EE4E10"
						})
					/*} else if (showGraph.value) {
						+LineChart(graphData[i].map { it?.value }, {
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
						}*/
					}
				}
			})
		}).drawAndHandleEvents(appState, context, skin)
	}
}

fun main(args: Array<String>) {
	val app = TimelineApp()
	app.loadData()
}

val baliTomeg = hashMapOf<Int, Ptr<Float>>()

//val graphData = array(initBali(), initMaki(), init_data(), init_data(), init_data())
//val graphAvgData = array(calc_ema(graphData[0], 0.9f), calc_ema(graphData[1], 0.9f), calc_ema(graphData[2], 0.9f), calc_ema(graphData[3], 0.9f), calc_ema(graphData[4], 0.9f))
val bestFitLines = array<List<Float>>(arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())

fun initBali(): List<Ptr<Float>?> = arrayListOf(80f, 79.5f, 79.0f, 78.4f, 78.3f, 78.5f, 78.6f, 78.4f,
		78.6f, 78.1f, null, 77.9f, 78f, 78f, null, 78.5f, 78.1f, 78f, 77.9f, 77.4f, 77.6f, 77.7f).map { if (it == null) null else Ptr(it) }

fun initMaki(): List<Ptr<Float>?> = arrayListOf(55f, 55.1f, 54.8f, 54.5f, 54.3f, 54.4f, null, 54.8f,
		54.9f, 54.5f, 53.9f, 53.8f, 53.6f, 53.7f, null, 54.7f, 54.4f, 54.0f, 53.9f, 53.8f, 53.5f, 53.7f).map { if (it == null) null else Ptr(it) }

val booleanValues = array(Ptr(true), Ptr(true), Ptr(false), Ptr(false), Ptr(false))
val showAvgValues = array(Ptr(true), Ptr(true), Ptr(false), Ptr(false), Ptr(false))

fun String.allocNew(): String {
	return StringBuilder().append(this).toString()
}

fun init_data(): MutableList<Ptr<Float>?> {
	var last = 30.0f;
	val data = ArrayList<Ptr<Float>?>(100000);
	for (i in 0..100000) {
		last = last + Math.random().toFloat() * 2.0f - 1.0f;
		if (last < 0) {
			last = 30f;
		} else if (last > 60f) {
			last = 30f;
		}
		data.add(Ptr(last));
	}
	return data;
}

fun calcBestFitLine(data: List<Float>, x1: Int, x2: Int): List<Float> {
	// n = x2-x1
	//     n*avg(x*y) - avg(x)*avg(y)
	// m =-------------------------
	//     n*avg(x^2) - avg(x)^2
	val n = x2-x1
	val avgX = (1..n).sum()
	// TODO: sublist nem működik JS alatt!
	val avgY = data.subList(x1, x2).sum() / n
	val avgXY = data.subList(x1, x2).withIndices().map { (it.first+1)*it.second }.sum()
	val avgX2 = (1..n).map{it*it}.sum()
	val avgX_2 = avgX * avgX
	val mUpper = n * avgXY - avgX * avgY
	val mLower = n*avgX2 - avgX_2
	val m = mUpper / mLower

	val bUpper = avgY * avgX2 - avgX*avgXY
	val b = bUpper / mLower
	return (0..n).map { it*m+b }.toArrayList()
}

fun calc_ema(data: Map<Int, Ptr<Float>>, smoothingConstant: Float): Map<Int, Float> {
	val avgData = HashMap<Int, Float>(data.size)
	if (data.isEmpty()) {
		return avgData
	}
	val sortedKeys = data.keySet().sort()
	val startIndex  = sortedKeys.first!!
	val endIndex  = sortedKeys.last!!
	var last_data = data[startIndex]!!.value;
	for (index in (startIndex..endIndex)) {
		val value = data[index]?.value
		val diff: Float = if (value == null) 0f else (value - last_data)
		val smoothing_percentage = 1f - smoothingConstant;
		val curr_data = last_data + smoothing_percentage * diff;
		avgData[index] = curr_data;
		last_data = curr_data;
	}
	return avgData
}
