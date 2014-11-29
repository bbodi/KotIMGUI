package widget.chart

import timeline.context

class LineChart(val data: List<Number>, init: LineChart.() -> Unit ) {
	var color = "green"
	{
		init()
	}

	fun draw(screenX: Int, screenY: Int, screenW: Int, screenH: Int, x1: Int, x2: Int) {
		val bottom = (screenY + screenH) - (screenH * 0.1f);
		val timeRange = x2 - x1
		val screenStepW = screenW / timeRange.toFloat();

		context.save()
		context.beginPath()
		context.rect(screenX, screenY, screenW, screenH)
		context.clip()
		context.moveTo(screenX, screenY)
		for ((i, v) in (x1..x2).withIndices()) {
			val data_index = (x1 + i).toInt()
			if (data_index < 0) {
				continue
			} else if (data_index >= data.size) {
				break
			}
			val value = (data[data_index].toFloat() * 10f).toInt()
			val valueY = bottom - 10 - value
			var x = (screenX + i * screenStepW).toInt()
			context.lineTo(x, valueY)
		}
		context.strokeStyle = color
		context.lineWidth = 1.0
		context.stroke()
		context.restore()
	}
}