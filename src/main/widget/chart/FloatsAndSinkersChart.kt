package widget.chart

import timeline.Timeline
import timeline.ChartDrawingAreaInfo
import timeline.TimelineData
import timeline.debugLines
import timeline.AppState
import kotlin.js.dom.html5.CanvasContext
import timeline.Ptr

class FloatsAndSinkersChart(val data: Map<Int, Ptr<Float>>, val trend: Map<Int, Float>, init: FloatsAndSinkersChart.() -> Unit): Chart {
	var actualDataColor = "green"
	var trendColor = "red"
	var lineWidth = 2.0

	{
		init()
	}

	override fun draw(context: CanvasContext, info: ChartDrawingAreaInfo) {
		plot(context, info, { (x, actualY, trendY) ->
			context.lineTo(x, trendY)
		})
		context.lineJoin = "round";
		context.strokeStyle = actualDataColor
		context.lineWidth = lineWidth
		context.stroke()
		plot(context, info, { (x, actualY, trendY) ->
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
		plot(context, info, { (x, actualY, trendY) ->
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


	private fun plot(context: CanvasContext, info: ChartDrawingAreaInfo, body: (Int, Float, Float)-> Unit) {
		context.beginPath()
		for ((i, v) in (info.leftRange.. info.rightRange).withIndices()) {
			val dataIndex = (info.leftRange + i).toInt()
			val trendValue = trend[dataIndex]
			if (trendValue == null) {
				continue
			}
			val actualValue = data[dataIndex]?.value ?: trendValue

			val actualY = info.chartAreaHeight - (actualValue - info.bottomRange) * (info.chartAreaHeight / info.valueRange)
			val trendY = info.chartAreaHeight - (trendValue - info.bottomRange) * (info.chartAreaHeight / info.valueRange)
			var x = (i * info.screenStepW).toInt()
			body(x, actualY, trendY)
		}
	}

	override fun handleEvents(state: AppState, info: ChartDrawingAreaInfo) {
		val mouseAxisX = state.mousePos.x - info.chartAreaX

		val timeRange = info.rightRange - info.leftRange
		val screenStepW = info.chartAreaWidth / timeRange.toFloat();
		val valueRange = (info.topRange - info.bottomRange).toFloat()

		var data_index = (info.leftRange + mouseAxisX / screenStepW).toInt()
		if (data[data_index] == null) {
			return
		}
		val value = data[data_index]!!.value
		val trend = trend[data_index]
		val valueY = (info.chartAreaHeight - (value.toInt() - info.bottomRange) * (info.chartAreaHeight / valueRange)).toInt()
		val mouseY = state.mousePos.y - info.chartAreaY
		debugLines.add("actual: $value, trend: $trend")
		if (Math.abs((mouseY - valueY).toDouble()) < 5) {
			lineWidth *= 2
		}
	}
}