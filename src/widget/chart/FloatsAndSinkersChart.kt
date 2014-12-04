package widget.chart

import timeline.context
import timeline.app
import timeline.Timeline
import timeline.ChartDrawingAreaInfo
import timeline.TimelineData
import timeline.debugLines

class FloatsAndSinkersChart(val data: List<Number?>, val trend: List<Number>, init: FloatsAndSinkersChart.() -> Unit): Chart {
	var actualDataColor = "green"
	var trendColor = "red"
	var lineWidth = 2.0

	{
		init()
	}

	override fun draw(info: ChartDrawingAreaInfo) {
		plot(info, { (x, actualY, trendY) ->
			context.lineTo(x, trendY)
		})
		context.lineJoin = "round";
		context.strokeStyle = actualDataColor
		context.lineWidth = lineWidth
		context.stroke()
		plot(info, { (x, actualY, trendY) ->
			if (actualY > trendY) {
				context.moveTo(x, actualY)
				context.arc(x, actualY, 2, 0, 2 * Math.PI, false)
				context.moveTo(x, actualY)
				context.lineTo(x, trendY)
			}
		})
		context.fillStyle = "red"
		context.strokeStyle = "red"
		context.stroke()
		context.fill()
		plot(info, { (x, actualY, trendY) ->
			if (actualY <= trendY) {
				context.moveTo(x, actualY)
				context.arc(x, actualY, 2, 0, 2 * Math.PI, false)
				context.moveTo(x, actualY)
				context.lineTo(x, trendY)
			}
		})
		context.fillStyle = "green"
		context.strokeStyle = "green"
		context.stroke()
		context.fill()
	}


	private fun plot(info: ChartDrawingAreaInfo, body: (Int, Float, Float)-> Unit) {
		context.beginPath()
		for ((i, v) in (info.leftRange.. info.rightRange).withIndices()) {
			val dataIndex = (info.leftRange + i).toInt()
			if (dataIndex < 0) {
				continue
			} else if (dataIndex >= trend.size) {
				break
			}
			val trendValue = trend[dataIndex].toFloat()
			val actualValue = data[dataIndex]?.toFloat() ?: trendValue

			val actualY = info.chartAreaHeight - (actualValue - info.bottomRange) * (info.chartAreaHeight / info.valueRange)
			val trendY = info.chartAreaHeight - (trendValue - info.bottomRange) * (info.chartAreaHeight / info.valueRange)
			var x = (i * info.screenStepW).toInt()
			body(x, actualY, trendY)
		}
	}

	override fun handleEvents(info: ChartDrawingAreaInfo) {
		val mouseAxisX = app.mousePos.x - info.chartAreaX

		val timeRange = info.rightRange - info.leftRange
		val screenStepW = info.chartAreaWidth / timeRange.toFloat();
		val valueRange = (info.topRange - info.bottomRange).toFloat()

		var data_index = (info.leftRange + mouseAxisX / screenStepW).toInt()
		if (data_index < 0 || data_index >= data.size) {
			return
		}
		if (data[data_index] == null) {
			return
		}
		val value = data[data_index]!!
		val trend = trend[data_index]
		val valueY = (info.chartAreaHeight - (value.toInt() - info.bottomRange) * (info.chartAreaHeight / valueRange)).toInt()
		val mouseY = app.mousePos.y - info.chartAreaY
		debugLines.add("actual: $value, trend: $trend")
		if (Math.abs((mouseY - valueY).toDouble()) < 5) {
			lineWidth *= 2
		}
	}
}