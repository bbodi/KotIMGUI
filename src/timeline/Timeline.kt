package timeline

import widget.Pos
import widget.Widget
import widget.chart.LineChart
import widget.Pos
import widget.PositionBasedId
import skin.Variant
import widget.chart.Chart
import widget.Rect

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

data class ChartDrawingAreaInfo(timeline: Timeline, data: TimelineData) {
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

	fun getYForValue(value: Float): Int = (chartAreaHeight - (value.toInt() - bottomRange) * (chartAreaHeight / valueRange)).toInt();

	{
		leftRange = data.leftRange.toInt()
		rightRange = data.rightRange.toInt()
		bottomRange = data.bottomRange.toInt()
		topRange = data.topRange.toInt()
		val timeRange = data.rightRange - data.leftRange
		this.screenStepW = timeline.width / timeRange.toFloat()
		val isApplicableForCurrentView = {(generator: XAxisLabelGenerator, secondRowGenerator: XAxisLabelGenerator) ->
			if (timeline.width / (timeRange / generator.rangeReducer) >= app.skin.charWidth * (generator.labelWidth + 1))
				array(generator, secondRowGenerator)
			else
				null
		}

		this.entryRenderers = isApplicableForCurrentView(XAxisLabelGenerator.DayLabelGenerator, XAxisLabelGenerator.MonthLabelGenerator)
				?: isApplicableForCurrentView(XAxisLabelGenerator.WeekLabelGenerator, XAxisLabelGenerator.MonthLabelGenerator)
				?: isApplicableForCurrentView(XAxisLabelGenerator.MonthLabelGenerator, XAxisLabelGenerator.YearLabelGenerator)
				?: isApplicableForCurrentView(XAxisLabelGenerator.ThreeMonthLabelGenerator, XAxisLabelGenerator.YearLabelGenerator)
				?: array(XAxisLabelGenerator.YearLabelGenerator)

		this.bottomForLabels = (timeline.pos.y + timeline.height) - (app.skin.rowHeight) - app.skin.panelBorder
		this.bottomForLines = (timeline.pos.y + timeline.height) - (app.skin.rowHeight * 3) - app.skin.panelBorder
		context.beginPath()
		this.valueRange = if (data.topRange > data.bottomRange) (data.topRange - data.bottomRange).toFloat() else 1f
		var valueReducer = 1
		while (true) {
			if (timeline.height / (this.valueRange / valueReducer) >= app.skin.rowHeight) {
				break
			}
			valueReducer *= 10
		}
		this.valueReducer = valueReducer
		val yAxisLabelLen = (data.topRange / this.valueReducer).toInt().toString().length
		this.chartAreaX = timeline.pos.x + app.skin.charWidth * (yAxisLabelLen + 1) + app.skin.panelBorder
		this.chartAreaY = timeline.pos.y + app.skin.panelBorder
		this.chartAreaWidth = timeline.width - app.skin.charWidth * (yAxisLabelLen + 1) - app.skin.panelBorder
		this.chartAreaHeight = timeline.height - (app.skin.rowHeight * 3) - (app.skin.panelBorder * 2)
		this.screenStepH = this.chartAreaHeight / this.valueRange
	}
}

class Timeline(pos: Pos, init: Timeline.() -> Unit = {}) : Widget(pos) {

	private var chartDrawingAreaInfo: ChartDrawingAreaInfo by kotlin.properties.Delegates.notNull()

	override var width = 0
		get() = parent!!.contentWidth
	override var height = app.skin.rowHeight
		private set
		get() = parent!!.contentHeight
	var charts: MutableList<Chart> = arrayListOf();

	{
		init()
	}

	var hover = false
		private set
		get() = app.mousePos.is_in_rect(pos, Pos(width, height))

	override fun draw() {
		app.skin.drawPanelRect(pos.x, pos.y, width, height, Variant.DEFAULT)

		val info = chartDrawingAreaInfo
		for ((i, v) in (info.bottomRange.. info.topRange).withIndices()) {
			val entry = v.toString()
			if (info.valueReducer == 1 || v % info.valueReducer == 0) {
				var y = info.bottomForLines - i * info.screenStepH
				context.moveTo(info.chartAreaX, y)
				context.lineTo(info.chartAreaX + info.chartAreaWidth, y)
				val textX = pos.x + app.skin.panelBorder
				var textY = info.bottomForLines - i * info.screenStepH - app.skin.charHeight / 2
				app.skin.text(entry, textX, textY, "white", app.skin.font)
			}
		}


		for ((i, v) in (info.leftRange..info.rightRange).withIndices()) {
			info.entryRenderers.reverse().withIndices().forEach {
				val entry = it.second.labelGenerator(v.toInt())
				if (entry != null) {
					if (it.first == info.entryRenderers.size - 1) {
						context.moveTo(info.chartAreaX + i * info.screenStepW, info.bottomForLines)
						context.lineTo(info.chartAreaX + i * info.screenStepW, info.chartAreaY)
					}
					val textX = info.chartAreaX + i * info.screenStepW - (entry.length * app.skin.charWidth / 2)
					var textY = info.bottomForLabels - it.first * app.skin.rowHeight
					app.skin.text(entry, textX, textY, "white", app.skin.font)
				}
			}
		}
		context.strokeStyle = "#676767"
		context.lineWidth = 1.0
		context.stroke()
		context.save()
		context.beginPath()
		context.rect(info.chartAreaX, info.chartAreaY, info.chartAreaWidth, info.chartAreaHeight)
		context.clip()
		context.translate(info.chartAreaX, info.chartAreaY)
		charts.forEach { it.handleEvents(info) }
		charts.forEach { it.draw(info) }
		val data = getOrCreateMyData()
		context.restore()
		if (data.areaSelectionRect != null) {
			with(data.areaSelectionRect!!, {context.rect(x, y, width, height)})
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

	override fun handleEvents() {
		val data = getOrCreateMyData()
		chartDrawingAreaInfo = ChartDrawingAreaInfo(this, data)

		val was_hot = app.hot_widget_id == id
		val was_active = app.active_widget_id == id
		val down = was_active && !app.leftMouseButton.just_released;

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
		val clickedJustNow = app.leftMouseButton.just_pressed && hover
		if (hover) {
			setCursor(CursorStyle.Grab)
		}
		if (clickedJustNow) {
			data.lastMousePos = app.mousePos
		} else if (down) {
			if (app.ctrl.down) {
				data.areaSelectionRect = Rect.fromPositions(data.lastMousePos, app.mousePos)
			} else {
				val (x, y) = projectIntoChartSpace(chartDrawingAreaInfo, data, app.mousePos)
				val (lastX, lastY) = projectIntoChartSpace(chartDrawingAreaInfo, data, data.lastMousePos)
				val deltaX = x.toInt() - lastX.toInt()
				data.leftRange -= deltaX
				data.rightRange -= deltaX
				val deltaY = lastY.toInt() - y.toInt()
				data.bottomRange += deltaY
				data.topRange += deltaY
				data.lastMousePos = app.mousePos
				setCursor(CursorStyle.Grabbing)
			}
		} else if (app.leftMouseButton.just_released && data.areaSelectionRect != null) {
			val areaSelectionRect = data.areaSelectionRect!!
			val (x1, y1) = projectIntoChartSpace(chartDrawingAreaInfo, data, areaSelectionRect.topLeft)
			val (x2, y2) = projectIntoChartSpace(chartDrawingAreaInfo, data, areaSelectionRect.bottomRight)
			data.leftRange = x1.toInt()
			data.rightRange = (x2+0.5f).toInt()
			data.bottomRange = y2.toInt()
			data.topRange = (y1+0.5f).toInt()
			data.areaSelectionRect = null
		}
		if (hover && app.mouseScrollDelta != 0) {
			if (app.ctrl.down) {
				data.bottomRange += app.mouseScrollDelta
				data.topRange -= app.mouseScrollDelta
				setCursor(CursorStyle.NorthResize)
			} else {
				setCursor(if (app.mouseScrollDelta > 0) CursorStyle.ZoomIn else CursorStyle.ZoomOut)
				data.leftRange += app.mouseScrollDelta
				data.rightRange -= app.mouseScrollDelta
			}
		}
	}

	fun Chart.plus() {
		charts.add(this)
	}

	private fun getOrCreateMyData(): TimelineData {
		val dataPtr = app.getWidgetData("Timeline", id)
		return if (dataPtr == null) {
			val data = TimelineData()
			app.setWidgetData("Timeline", id, data)
			data
		} else {
			dataPtr as TimelineData
		}
	}
}