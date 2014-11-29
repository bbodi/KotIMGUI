package widget.chart

import timeline.context

class LineChart(val data: List<Number>, init: LineChart.() -> Unit ) {
	var color = "green"
	{
		init()
	}

	fun draw(screenW: Int, screenH: Int, x1: Int, x2: Int, y1: Int, y2: Int) {
		val timeRange = x2 - x1
		val screenStepW = screenW / timeRange.toFloat();
		val valueRange = (y2 - y1).toFloat()

		context.beginPath()
		for ((i, v) in (x1..x2).withIndices()) {
			val data_index = (x1 + i).toInt()
			if (data_index < 0) {
				continue
			} else if (data_index >= data.size) {
				break
			}
			val value = data[data_index].toFloat()
			val valueY = screenH - (value - y1) * (screenH / valueRange)
			var x = (i * screenStepW).toInt()
			context.lineTo(x, valueY)
		}
		context.strokeStyle = color
		context.lineWidth = 1.0
		context.stroke()
	}
}