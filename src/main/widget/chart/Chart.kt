package widget.chart

import timeline.Timeline
import timeline.ChartDrawingAreaInfo
import timeline.TimelineData
import timeline.AppState
import timeline.AppSizeMetricData

trait Chart {
	fun draw(info: ChartDrawingAreaInfo)
	fun handleEvents(state: AppState, info: ChartDrawingAreaInfo)
}
