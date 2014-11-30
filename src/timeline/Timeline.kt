package timeline

import widget.Pos
import widget.Widget
import widget.chart.LineChart
import widget.AbsolutePos
import widget.PositionBasedId
import skin.Variant

class TimelineData {
	var x1 = 100
	var x2 = 200
	var y1 = 0
	var y2 = 120
	var lastMousePos = AbsolutePos(0, 0)
}

private val monthNames: Array<String> = array("Jan", "Feb", "Mar", "Apr", "May", "Jun",
		"Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

private enum class XAxisLabelGenerator(val labelWidth: Int, val rangeReducer: Int, val labelGenerator: (day: Int) -> String?) {

	DayLabelGenerator : XAxisLabelGenerator(2, 1, { day ->
		val date = Date(day * 24 * 60 * 60 * 1000)
		date.getDayOfMonth().toString()
	})
	WeekLabelGenerator : XAxisLabelGenerator(2, 7, { day ->
		val date = Date(day * 24 * 60 * 60 * 1000)
		if (date.getDayOfWeek() != 0) null else date.getDayOfMonth().toString()
	})
	MonthLabelGenerator : XAxisLabelGenerator(3, 30, { day ->
		val date = Date(day * 24 * 60 * 60 * 1000)
		if (date.getDayOfMonth() != 1) null else monthNames[date.getMonth()]
	})
	ThreeMonthLabelGenerator : XAxisLabelGenerator(3, 90, { day ->
		val date = Date(day * 24 * 60 * 60 * 1000)
		if (date.getMonth() % 3 != 0 || date.getDayOfMonth() != 1) null else monthNames[date.getMonth()]
	})
	YearLabelGenerator : XAxisLabelGenerator(4, 365, { day ->
		val date = Date(day * 24 * 60 * 60 * 1000)
		if (date.getMonth() != 0 || date.getDayOfMonth() != 1) null else date.getFullYear().toString()
	})
}

class Timeline(pos: Pos, init: Timeline.() -> Unit = {}) : Widget(pos) {

	override var width = 0
		get() = parent!!.contentWidth
	override var height = widgetHandler.skin.rowHeight
		private set
		get() = parent!!.contentHeight
	var charts: MutableList<LineChart> = arrayListOf();

	var chartAreaX = 0
	var chartAreaY = 0

	{
		init()
	}
	override val id: Int = PositionBasedId(pos.x, pos.y, 0).hashCode()

	var hover = false
		private set
		get() = widgetHandler.mousePos.is_in_rect(pos, AbsolutePos(width, height))

	override fun draw() {
		widgetHandler.skin.drawPanelRect(pos.x, pos.y, width, height, Variant.DEFAULT)

		val data = getOrCreateMyData()
		data.y1 = intValues[0].data
		data.y2 = intValues[1].data

		debugLines.add("Timeline - x1: ${data.x1}, x2: ${data.x2}")
		debugLines.add(IntValue(12))
		val timeRange = data.x2 - data.x1
		val screenStepW = width / timeRange.toFloat()
		val isApplicableForCurrentView = {(generator: XAxisLabelGenerator, secondRowGenerator: XAxisLabelGenerator) ->
			if (width / (timeRange / generator.rangeReducer) >= widgetHandler.skin.charWidth * (generator.labelWidth + 1))
				array(generator, secondRowGenerator)
			else
				null
		}

		val entryRenderers = isApplicableForCurrentView(XAxisLabelGenerator.DayLabelGenerator, XAxisLabelGenerator.MonthLabelGenerator)
				?: isApplicableForCurrentView(XAxisLabelGenerator.WeekLabelGenerator, XAxisLabelGenerator.MonthLabelGenerator)
				?: isApplicableForCurrentView(XAxisLabelGenerator.MonthLabelGenerator, XAxisLabelGenerator.YearLabelGenerator)
				?: isApplicableForCurrentView(XAxisLabelGenerator.ThreeMonthLabelGenerator, XAxisLabelGenerator.YearLabelGenerator)
				?: array(XAxisLabelGenerator.YearLabelGenerator)

		val bottomForLabels = (pos.y + height) - (widgetHandler.skin.rowHeight) - widgetHandler.skin.panelBorder
		val bottomForLines = (pos.y + height) - (widgetHandler.skin.rowHeight * 3) - widgetHandler.skin.panelBorder
		context.beginPath()
		val valueRange = (data.y2 - data.y1).toFloat()
		val screenStepH = height / valueRange
		var valueReducer = 1
		while (true) {
			if (height / (valueRange / valueReducer) >= widgetHandler.skin.rowHeight) {
				break
			}
			valueReducer *= 10
		}
		val yAxisLabelLen = (data.y2 / valueReducer).toString().length
		chartAreaX = pos.x + widgetHandler.skin.charWidth * (yAxisLabelLen + 1) + widgetHandler.skin.panelBorder
		chartAreaY = pos.y + widgetHandler.skin.panelBorder
		val chartAreaWidth = width - widgetHandler.skin.charWidth * (yAxisLabelLen + 1) - widgetHandler.skin.panelBorder
		val chartAreaHeight = height - (widgetHandler.skin.rowHeight * 3) - (widgetHandler.skin.panelBorder*2)
		for ((i, v) in (data.y1..data.y2).withIndices()) {
			val entry = v.toString()
			if (valueReducer == 1 || v % valueReducer == 0) {
				var y = bottomForLines - i * screenStepH
				context.moveTo(chartAreaX, y)
				context.lineTo(chartAreaX + chartAreaWidth, y)
				val textX = pos.x + widgetHandler.skin.panelBorder
				var textY = bottomForLines - i * screenStepH - widgetHandler.skin.charHeight / 2
				widgetHandler.skin.text(entry, textX, textY, "white", widgetHandler.skin.font)
			}
		}


		for ((i, v) in (data.x1..data.x2).withIndices()) {
			entryRenderers.reverse().withIndices().forEach {
				val entry = it.second.labelGenerator(v)
				if (entry != null) {
					if (it.first == entryRenderers.size - 1) {
						context.moveTo(chartAreaX + i * screenStepW, bottomForLines)
						context.lineTo(chartAreaX + i * screenStepW, chartAreaY)
					}
					val textX = chartAreaX + i * screenStepW - (entry.length * widgetHandler.skin.charWidth / 2)
					var textY = bottomForLabels - it.first * widgetHandler.skin.rowHeight
					widgetHandler.skin.text(entry, textX, textY, "white", widgetHandler.skin.font)
				}
			}
		}
		context.strokeStyle = "#676767"
		context.lineWidth = 1.0
		context.stroke()
		context.save()
		context.beginPath()
		context.rect(chartAreaX, chartAreaY, chartAreaWidth, chartAreaHeight)
		context.clip()
		context.translate(chartAreaX, chartAreaY)
		charts.forEach { it.draw(chartAreaWidth, chartAreaHeight, data.x1, data.x2, data.y1, data.y2) }
		context.restore()
		charts.forEach { it.handleEvents(this, chartAreaWidth, chartAreaHeight, data.x1, data.x2, data.y1, data.y2) }
	}

	override fun handleEvents() {
		val was_hot = widgetHandler.hot_widget_id == id
		val was_active = widgetHandler.active_widget_id == id
		val down = was_active && !widgetHandler.leftMouseButton.just_released;

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
		val clicked = widgetHandler.leftMouseButton.just_pressed && hover
		val data = getOrCreateMyData()
		if (hover) {
			setCursor(CursorStyle.Grab)
		}
		if (clicked) {
			data.lastMousePos = widgetHandler.mousePos
		} else if (down) {
			val deltaX = widgetHandler.mousePos.x - data.lastMousePos.x
			data.x1 -= deltaX
			data.x2 -= deltaX
			val deltaY = widgetHandler.mousePos.y - data.lastMousePos.y
			data.y1 += deltaY
			data.y2 += deltaY
			intValues[0].data = data.y1
			intValues[1].data = data.y2
			data.lastMousePos = widgetHandler.mousePos
			setCursor(CursorStyle.Grabbing)
		}
		if (hover && widgetHandler.mouseScrollDelta != 0) {
			val asd = widgetHandler.shift
			if (widgetHandler.shift.down) {
				data.y1 += widgetHandler.mouseScrollDelta
				data.y2 -= widgetHandler.mouseScrollDelta
				intValues[0].data = data.y1
				intValues[1].data = data.y2
				setCursor(CursorStyle.NorthResize)
			} else {
				setCursor(if (widgetHandler.mouseScrollDelta > 0) CursorStyle.ZoomIn else CursorStyle.ZoomOut)
				data.x1 += widgetHandler.mouseScrollDelta
				data.x2 -= widgetHandler.mouseScrollDelta
			}
		}
	}

	fun LineChart.plus() {
		charts.add(this)
	}

	private fun getOrCreateMyData(): TimelineData {
		val dataPtr = widgetHandler.getWidgetData("Timeline", id)
		return if (dataPtr == null) {
			val data = TimelineData()
			widgetHandler.setWidgetData("Timeline", id, data)
			data
		} else {
			dataPtr as TimelineData
		}
	}
}