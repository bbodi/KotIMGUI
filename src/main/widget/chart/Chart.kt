package widget.chart

import timeline.Timeline
import timeline.ChartDrawingAreaInfo
import timeline.TimelineData
import timeline.AppState
import timeline.AppSizeMetricData
import kotlin.js.dom.html5.CanvasContext

trait Chart {
	fun draw(context: CanvasContext, info: ChartDrawingAreaInfo)
	fun handleEvents(state: AppState, info: ChartDrawingAreaInfo)
}
