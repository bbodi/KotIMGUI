package widget.chart

import timeline.context
import timeline.Timeline
import timeline.ChartDrawingAreaInfo
import timeline.TimelineData
import timeline.AppState

class LineChart(val data: List<Number?>, init: LineChart.() -> Unit): Chart {
	var color = "green"
	var lineWidth = 2.0

	{
		init()
	}

	override fun draw(info: ChartDrawingAreaInfo) {
		val timeRange = info.rightRange - info.leftRange
		val screenStepW = info.chartAreaWidth / timeRange.toFloat();
		val valueRange = (info.topRange - info.bottomRange).toFloat()

		context.beginPath()
		for ((i, v) in (info.leftRange..info.rightRange).withIndices()) {
			val dataIndex = (info.leftRange + i).toInt()
			if (dataIndex < 0) {
				continue
			} else if (dataIndex >= data.size) {
				break
			}
			val value = data[dataIndex]
			if (value == null) {
				continue
			}
			val valueY = info.chartAreaHeight - (value.toFloat() - info.bottomRange) * (info.chartAreaHeight / valueRange)
			var x = (i * screenStepW).toInt()
			context.lineTo(x, valueY)
		}
		context.lineJoin = "round";
		context.strokeStyle = color
		context.lineWidth = lineWidth
		context.stroke()
	}

	override fun handleEvents(state: AppState, info: ChartDrawingAreaInfo) {
		val mouseAxisX = state.mousePos.x - info.chartAreaX
		val timeRange = info.rightRange - info.leftRange
		val screenStepW = info.chartAreaWidth / timeRange.toFloat();

		var data_index = (info.leftRange + mouseAxisX / screenStepW).toInt()
		if (data_index < 0 || data_index >= data.size) {
			return
		}
		val value = data[data_index]
		if (value == null) {
			return
		}
		val valueY = info.getYForValue(value.toFloat())
		val mouseY = state.mousePos.y - info.chartAreaY
		if (Math.abs((mouseY - valueY).toDouble()) < 5) {
			lineWidth *= 2
		}
	}
}