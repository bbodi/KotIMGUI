package widget.chart

import timeline.Timeline
import timeline.ChartDrawingAreaInfo
import timeline.TimelineData

trait Chart {
	fun draw(info: ChartDrawingAreaInfo)
	fun handleEvents(info: ChartDrawingAreaInfo)
}
