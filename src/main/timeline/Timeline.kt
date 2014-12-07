package timeline

import widget.Pos
import widget.Widget
import widget.chart.LineChart
import widget.Pos
import widget.PositionBasedId
import skin.Variant
import widget.chart.Chart
import widget.Rect
import skin.Skin

class TimelineData {
	var leftRange: Int = 100
	var rightRange: Int = 200
	var bottomRange: Int = 0
	var topRange: Int = 120
	var lastMousePos = Pos(0, 0)
	var areaSelectionRect: Rect? = null
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

data class ChartDrawingAreaInfo(timeline: Timeline, data: TimelineData, metrics: AppSizeMetricData) {
	val valueRange: Float

	val entryRenderers: Array<XAxisLabelGenerator>

	val screenStepW: Float

	val chartAreaX: Int
	val chartAreaY: Int
	val chartAreaWidth: Int
	val chartAreaHeight: Int
	val bottomForLabels: Int
	val bottomForLines: Int
	val valueReducer: Int
	val screenStepH: Float
	val leftRange: Int
	val rightRange: Int
	val bottomRange: Int
	val topRange: Int

	var persistentData: TimelineData = TimelineData()

	fun getYForValue(value: Float): Int = (chartAreaHeight - (value.toInt() - bottomRange) * (chartAreaHeight / valueRange)).toInt();

	{
		leftRange = data.leftRange.toInt()
		rightRange = data.rightRange.toInt()
		bottomRange = data.bottomRange.toInt()
		topRange = data.topRange.toInt()
		val timeRange = data.rightRange - data.leftRange
		this.screenStepW = timeline.width / timeRange.toFloat()
		val isApplicableForCurrentView = {(generator: XAxisLabelGenerator, secondRowGenerator: XAxisLabelGenerator) ->
			if (timeline.width / (timeRange / generator.rangeReducer) >= metrics.charWidth * (generator.labelWidth + 1))
				array(generator, secondRowGenerator)
			else
				null
		}

		this.entryRenderers = isApplicableForCurrentView(XAxisLabelGenerator.DayLabelGenerator, XAxisLabelGenerator.MonthLabelGenerator)
				?: isApplicableForCurrentView(XAxisLabelGenerator.WeekLabelGenerator, XAxisLabelGenerator.MonthLabelGenerator)
				?: isApplicableForCurrentView(XAxisLabelGenerator.MonthLabelGenerator, XAxisLabelGenerator.YearLabelGenerator)
				?: isApplicableForCurrentView(XAxisLabelGenerator.ThreeMonthLabelGenerator, XAxisLabelGenerator.YearLabelGenerator)
				?: array(XAxisLabelGenerator.YearLabelGenerator)

		this.bottomForLabels = (timeline.pos.y + timeline.height) - (metrics.rowHeight) - metrics.panelBorder
		this.bottomForLines = (timeline.pos.y + timeline.height) - (metrics.rowHeight * 3) - metrics.panelBorder
		context.beginPath()
		this.valueRange = if (data.topRange > data.bottomRange) (data.topRange - data.bottomRange).toFloat() else 1f
		var valueReducer = 1
		while (true) {
			if (timeline.height / (this.valueRange / valueReducer) >= metrics.rowHeight) {
				break
			}
			valueReducer *= 10
		}
		this.valueReducer = valueReducer
		val yAxisLabelLen = (data.topRange / this.valueReducer).toInt().toString().length
		this.chartAreaX = timeline.pos.x + metrics.charWidth * (yAxisLabelLen + 1) + metrics.panelBorder
		this.chartAreaY = timeline.pos.y + metrics.panelBorder
		this.chartAreaWidth = timeline.width - metrics.charWidth * (yAxisLabelLen + 1) - metrics.panelBorder
		this.chartAreaHeight = timeline.height - (metrics.rowHeight * 3) - (metrics.panelBorder * 2)
		this.screenStepH = this.chartAreaHeight / this.valueRange
	}
}

class Timeline(pos: Pos, override var width: Int, override var height: Int, val metrics: AppSizeMetricData, init: Timeline.() -> Unit = {}) : Widget(pos) {

	private var chartDrawingAreaInfo: ChartDrawingAreaInfo by kotlin.properties.Delegates.notNull()

	var charts: MutableList<Chart> = arrayListOf();

	{
		init()
	}

	var hover = false

	override fun draw(skin: Skin) {
		skin.drawPanelRect(pos.x, pos.y, width, height, Variant.DEFAULT)

		val persistentData = chartDrawingAreaInfo.persistentData
		for ((i, v) in (chartDrawingAreaInfo.bottomRange.. chartDrawingAreaInfo.topRange).withIndices()) {
			val entry = v.toString()
			if (chartDrawingAreaInfo.valueReducer == 1 || v % chartDrawingAreaInfo.valueReducer == 0) {
				var y = chartDrawingAreaInfo.bottomForLines - i * chartDrawingAreaInfo.screenStepH
				context.moveTo(chartDrawingAreaInfo.chartAreaX, y)
				context.lineTo(chartDrawingAreaInfo.chartAreaX + chartDrawingAreaInfo.chartAreaWidth, y)
				val textX = pos.x + metrics.panelBorder
				var textY = chartDrawingAreaInfo.bottomForLines - i * chartDrawingAreaInfo.screenStepH - metrics.charHeight / 2
				skin.text(entry, textX, textY, "white", metrics.font)
			}
		}


		for ((i, v) in (chartDrawingAreaInfo.leftRange..chartDrawingAreaInfo.rightRange).withIndices()) {
			chartDrawingAreaInfo.entryRenderers.reverse().withIndices().forEach {
				val entry = it.second.labelGenerator(v.toInt())
				if (entry != null) {
					if (it.first == chartDrawingAreaInfo.entryRenderers.size - 1) {
						context.moveTo(chartDrawingAreaInfo.chartAreaX + i * chartDrawingAreaInfo.screenStepW, chartDrawingAreaInfo.bottomForLines)
						context.lineTo(chartDrawingAreaInfo.chartAreaX + i * chartDrawingAreaInfo.screenStepW, chartDrawingAreaInfo.chartAreaY)
					}
					val textX = chartDrawingAreaInfo.chartAreaX + i * chartDrawingAreaInfo.screenStepW - (entry.length * metrics.charWidth / 2)
					var textY = chartDrawingAreaInfo.bottomForLabels - it.first * metrics.rowHeight
					skin.text(entry, textX, textY, "white", metrics.font)
				}
			}
		}
		context.strokeStyle = "#676767"
		context.lineWidth = 1.0
		context.stroke()
		context.save()
		context.beginPath()
		context.rect(chartDrawingAreaInfo.chartAreaX, chartDrawingAreaInfo.chartAreaY, chartDrawingAreaInfo.chartAreaWidth, chartDrawingAreaInfo.chartAreaHeight)
		context.clip()
		context.translate(chartDrawingAreaInfo.chartAreaX, chartDrawingAreaInfo.chartAreaY)
		charts.forEach { it.draw(chartDrawingAreaInfo) }
		context.restore()
		if (persistentData.areaSelectionRect != null) {
			with(persistentData.areaSelectionRect!!, {context.rect(x, y, width, height)})
			context.lineWidth = 2.0
			context.stroke()
		}
	}

	private fun projectIntoChartSpace(info: ChartDrawingAreaInfo, data: TimelineData, pos: Pos): Pair<Float, Float> {
		val mouseAxisX = pos.x - info.chartAreaX
		val mouseAxisY = pos.y - info.chartAreaY
		val timeRange = data.rightRange - data.leftRange
		val valueRange = (data.topRange - data.bottomRange).toFloat()
		val screenStepW = info.chartAreaWidth / timeRange.toFloat();
		val screenStepH = info.chartAreaHeight / valueRange.toFloat();
		return Pair(data.leftRange + mouseAxisX / screenStepW, data.topRange - mouseAxisY / screenStepH)
	}

	override fun handleEvents(state: AppState) {
		hover = state.mousePos.isInRect(pos, Pos(width, height))
		val persistentData = getOrCreateMyData(state)
		chartDrawingAreaInfo = ChartDrawingAreaInfo(this, persistentData, metrics)

		val was_hot = state.hot_widget_id == id
		val was_active = state.active_widget_id == id
		val down = was_active && !state.leftMouseButton.just_released;

		if (state.leftMouseButton.down && hover && !was_active) {
			state.active_widget_id = id
		} else if (was_active && state.leftMouseButton.just_released) {
			state.active_widget_id = null
		}

		if (hover && !was_hot) {
			state.hot_widget_id = id
		} else if (was_hot && !hover) {
			state.hot_widget_id = null
		}
		val clickedJustNow = state.leftMouseButton.just_pressed && hover
		if (hover) {
			setCursor(CursorStyle.Grab)
		}
		if (clickedJustNow) {
			persistentData.lastMousePos = state.mousePos
		} else if (down) {
			if (state.isKeyDown(Keys.Ctrl)) {
				persistentData.areaSelectionRect = Rect.fromPositions(persistentData.lastMousePos, state.mousePos)
			} else {
				val (x, y) = projectIntoChartSpace(chartDrawingAreaInfo, persistentData, state.mousePos)
				val (lastX, lastY) = projectIntoChartSpace(chartDrawingAreaInfo, persistentData, persistentData.lastMousePos)
				val deltaX = x.toInt() - lastX.toInt()
				persistentData.leftRange -= deltaX
				persistentData.rightRange -= deltaX
				val deltaY = lastY.toInt() - y.toInt()
				persistentData.bottomRange += deltaY
				persistentData.topRange += deltaY
				persistentData.lastMousePos = state.mousePos
				setCursor(CursorStyle.Grabbing)
			}
		} else if (state.leftMouseButton.just_released && persistentData.areaSelectionRect != null) {
			val areaSelectionRect = persistentData.areaSelectionRect!!
			val (x1, y1) = projectIntoChartSpace(chartDrawingAreaInfo, persistentData, areaSelectionRect.topLeft)
			val (x2, y2) = projectIntoChartSpace(chartDrawingAreaInfo, persistentData, areaSelectionRect.bottomRight)
			persistentData.leftRange = x1.toInt()
			persistentData.rightRange = (x2+0.5f).toInt()
			persistentData.bottomRange = y2.toInt()
			persistentData.topRange = (y1+0.5f).toInt()
			persistentData.areaSelectionRect = null
		}
		if (hover && state.mouseScrollDelta != 0) {
			if (state.isKeyDown(Keys.Ctrl)) {
				persistentData.bottomRange += state.mouseScrollDelta
				persistentData.topRange -= state.mouseScrollDelta
				setCursor(CursorStyle.NorthResize)
			} else {
				setCursor(if (state.mouseScrollDelta > 0) CursorStyle.ZoomIn else CursorStyle.ZoomOut)
				persistentData.leftRange += state.mouseScrollDelta
				persistentData.rightRange -= state.mouseScrollDelta
			}
		}
		charts.forEach { it.handleEvents(state, chartDrawingAreaInfo) }
	}

	fun Chart.plus() {
		charts.add(this)
	}

	private fun getOrCreateMyData(state: AppState): TimelineData {
		val dataPtr = state.getWidgetData("Timeline", id)
		return if (dataPtr == null) {
			val persistentData = TimelineData()
			state.setWidgetData("Timeline", id, persistentData)
			persistentData
		} else {
			dataPtr as TimelineData
		}
	}
}