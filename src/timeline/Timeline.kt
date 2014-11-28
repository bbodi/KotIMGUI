package timeline

import widget.Pos
import widget.Widget
import widget.chart.LineChart
import widget.AbsolutePos
import widget.PositionBasedId
import skin.Variant

class TimelineData {
	var zoomLevel = 100
	var xAxisPos = 10000
	var lastMousePos = AbsolutePos(0, 0)
}

class Timeline(pos: Pos, init: Timeline.() -> Unit = {}) : Widget(pos) {
	override var width = 0
		get() = parent!!.contentWidth
	override var height = widgetHandler.skin.rowHeight
		private set
		get() = parent!!.contentHeight
	var charts: MutableList<LineChart> = arrayListOf();

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

		val a = (data.zoomLevel / Math.sqrt(3.0))
		val x1 = (data.xAxisPos - a).toInt()
		val x2 = (data.xAxisPos + a + 0.5).toInt()
		val timeRange = x2 - x1
		val screenStepW = width / (2f * a)
		val asd = if (width/timeRange >= widgetHandler.skin.charWidth * 3) {
			"Day"
		} else if (width/(timeRange/7) >= widgetHandler.skin.charWidth * 3) {
			"Week"
		} else if (width/(timeRange/30) >= widgetHandler.skin.charWidth * 3) {
			"Mon"
		} else if (width/(timeRange/90) * screenStepW >= widgetHandler.skin.charWidth * 3) {
			"3Mon"
		} else {
			"Year"
		}
		val q = Date(1)
		val qe = Date(1).getDayOfMonth()
		val bottom = (pos.y + height) - widgetHandler.skin.rowHeight
		context.beginPath()
		for ((i, v) in (x1..x2).withIndices()) {
			val draw = when (asd) {
				"Day" -> v.toString()
				"Week" -> with(Date(v*24*60*60*1000), {if (getDayOfWeek() == 0) v.toString() else null})
				"Mon" -> with(Date(v*24*60*60*1000), { if (getDayOfMonth() == 1) getMonth().toString() else null})
				"3Mon" -> with(Date(v*24*60*60*1000), {if (getMonth() % 3 == 0 && getDayOfMonth() == 1) getMonth().toString() else null})
				else -> with(Date(v*24*60*60*1000),  {if (getMonth() == 0 && getDayOfMonth() == 1) getFullYear().toString() else null})
			}
			if (draw != null) {
				context.moveTo(pos.x + i * screenStepW, bottom)
				context.lineTo(pos.x + i * screenStepW, pos.y)
				val textX = pos.x + i * screenStepW - (draw.length*widgetHandler.skin.charWidth/2)
				widgetHandler.skin.text(draw, textX, bottom, "white", widgetHandler.skin.font)
			}
		}
		context.strokeStyle = "#676767"
		context.lineWidth = 1.0
		context.stroke()


		charts.forEach { it.draw(pos.x, pos.y, width, height, data.zoomLevel, data.xAxisPos) }
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
		if (clicked) {
			data.lastMousePos = widgetHandler.mousePos
			setCursor(CursorStyle.Move)
		} else if (down) {
			val deltaX = widgetHandler.mousePos.x - data.lastMousePos.x
			data.xAxisPos -= deltaX
			data.lastMousePos = widgetHandler.mousePos
			setCursor(CursorStyle.Move)
		}
		if (hover && widgetHandler.mouseScrollDelta != 0) {
			data.zoomLevel = (data.zoomLevel - widgetHandler.mouseScrollDelta * 10).at_least(10)
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