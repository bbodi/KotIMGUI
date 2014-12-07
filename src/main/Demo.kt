package timeline

import java.util.ArrayList
import jquery.*
import kotlin.js.dom.html.window
import kotlin.js.dom.html.HTMLElement
import kotlin.js.dom.html.HTMLImageElement
import kotlin.js.dom.html5.CanvasContext
import kotlin.js.dom.html5.CanvasGradient
import kotlin.js.dom.html5.HTMLCanvasElement
import widget.Pos
import widget.Button
import widget.VScrollBar
import widget.HScrollBar
import skin.Variant
import widget.Panel
import widget.Pos
import widget.Textfield
import skin.DiscoverUI
import widget.ActionItem
import widget.ActionMenu
import widget.Widget
import widget.Checkbox
import widget.RadioButton
import widget.TabPanel
import timeline.Timeline
import widget.chart.LineChart
import widget.Label
import widget.NumberField
import widget.chart.FloatsAndSinkersChart
import skin.Skin

val value = IntValue(50)
val zoom_value = IntValue(50)
val strValue = StrValue("")
val strValue1 = StrValue("")
val strValue2 = StrValue("")
val strValue3 = StrValue("")
val strValue4 = StrValue("")
val strValue5 = StrValue("")
val booleanValues = array<BooleanValue>(BooleanValue(true), BooleanValue(true), BooleanValue(false), BooleanValue(false), BooleanValue(false))
val showAvgValues = array<BooleanValue>(BooleanValue(true), BooleanValue(false), BooleanValue(false), BooleanValue(false), BooleanValue(false))
val radioButtonValue = IntValue(0)
val tabPanelValue = IntValue(0)
val booleanValue = BooleanValue(true)
var leftMouseDown = false;
var middleMouseDown = false;
var rightMouseDown = false;
var showActionMenu = false

var actionMenuPos = Pos(0, 0)



fun String.allocNew(): String {
	return StringBuilder().append(this).toString()
}

fun init_data(): MutableList<Float> {
	var last = 30.0f;
	val data = ArrayList<Float>(100000);
	for (i in 0..100000) {
		last = last + Math.random().toFloat() * 2.0f - 1.0f;
		if (last < 0) {
			last = 30f;
		} else if (last > 60f) {
			last = 30f;
		}
		data.add(last);
	}
	return data;
}

fun calcBestFitLine(data: List<Float>, x1: Int, x2: Int): List<Float> {
	// n = x2-x1
	//     n*avg(x*y) - avg(x)*avg(y)
	// m =-------------------------
	//     n*avg(x^2) - avg(x)^2
	val n = x2-x1
	val avgX = (1..n).sum()
	// TODO: sublist nem működik JS alatt!
	val avgY = data.subList(x1, x2).sum() / n
	val avgXY = data.subList(x1, x2).withIndices().map { (it.first+1)*it.second }.sum()
	val avgX2 = (1..n).map{it*it}.sum()
	val avgX_2 = avgX * avgX
	val mUpper = n * avgXY - avgX * avgY
	val mLower = n*avgX2 - avgX_2
	val m = mUpper / mLower

	val bUpper = avgY * avgX2 - avgX*avgXY
	val b = bUpper / mLower
	return (0..n).map { it*m+b }.toArrayList()
}

fun calc_ema(data: List<Float?>, smoothingConstant: Float): List<Float> {
	val avgData = ArrayList<Float>(data.size)
	var last_data = data[0]!!;
	for (v in data) {
		val diff: Float = if (v == null) 0f else (v - last_data)
		val smoothing_percentage = 1f - smoothingConstant;
		val curr_data = last_data + smoothing_percentage * diff;
		avgData.add(curr_data);
		last_data = curr_data;
	}
	return avgData
}



private fun demoAppLogic(app: Application, state: AppState) {
	Panel(Pos(300, 630), state.metrics, {
		+Checkbox("Bali", booleanValues[0], downAlongLeftMargin(10), state.metrics)
		+Checkbox("Makika", booleanValues[1], downAlongLeftMargin(10), state.metrics, {
			variant = Variant.INFO
		})
		+Checkbox("Warning", booleanValues[2], downAlongLeftMargin(10), state.metrics, {
			variant = Variant.WARNING
		})
		+Checkbox("Avg", showAvgValues[2], toRightFromLastWidget(10), state.metrics, {
			disabled = booleanValues[2].value == false
		})
		+Checkbox("Error", booleanValues[3], downAlongLeftMargin(10), state.metrics, {
			variant = Variant.DANGER
		})
		+Checkbox("Avg", showAvgValues[3], toRightFromLastWidget(10), state.metrics, {
			disabled = booleanValues[3].value == false
		})
		+Checkbox("Success", booleanValues[4], downAlongLeftMargin(10), state.metrics, {
			variant = Variant.SUCCESS
		})
		+Checkbox("Avg", showAvgValues[4], toRightFromLastWidget(10), state.metrics, {
			disabled = booleanValues[4].value == false
		})
		+Checkbox("Disabled", booleanValues[0], downAlongLeftMargin(10), state.metrics, {
			disabled = true
		})
	}).drawAndHandleEvents(state, app.skin)

	Panel(Pos(500, 430), state.metrics, {
		+RadioButton("Default", radioButtonValue, 0, downAlongLeftMargin(10), state.metrics)
		+RadioButton("Info", radioButtonValue, 1, downAlongLeftMargin(10), state.metrics, {
			variant = Variant.INFO
		})
		+RadioButton("Warning", radioButtonValue, 2, downAlongLeftMargin(10), state.metrics, {
			variant = Variant.WARNING
		})
		+RadioButton("Danger", radioButtonValue, 3, downAlongLeftMargin(10), state.metrics, {
			variant = Variant.DANGER
		})
		+RadioButton("Success", radioButtonValue, 4, downAlongLeftMargin(10), state.metrics, {
			variant = Variant.SUCCESS
		})
		+RadioButton("Disabled", radioButtonValue, 5, downAlongLeftMargin(10), state.metrics, {
			disabled = true
		})
	}).drawAndHandleEvents(state, app.skin)

	val tabPanel = tabPanel(state.metrics, state)
	Panel(Pos(tabPanel.pos.x, tabPanel.pos.y), state.metrics, {

	})

	if (state.rightMouseButton.just_pressed) {
		showActionMenu = true
		actionMenuPos = state.mousePos
	}

	if (showActionMenu) {
		var parentActionItem: ActionItem? = null
		val contextMenu = ActionMenu(actionMenuPos, state.metrics, {
			+ActionItem("Normal", downAlongLeftMargin(), state.metrics, {
				comment = "Ctrl+N"
			})
			+ActionItem("Disabled", downAlongLeftMargin(), state.metrics, {
				disabled = true
			})
			+ActionItem("Checkbox value", downAlongLeftMargin(), state.metrics, {
				checkBoxValue = booleanValue
			})
			parentActionItem = ActionItem("Parent", downAlongLeftMargin(), state.metrics, {
				hasSubMenu = true
			})
			+parentActionItem!!
			+Textfield(strValue, 10, downAlongLeftMargin(), state.metrics, {
				width = 200
			})
			+Button("Start", downAlongLeftMargin(), state.metrics, {
				width = 200
			})
		})
		contextMenu.drawAndHandleEvents(state, app.skin)

		val subMenu = ActionMenu(parentActionItem!!.pos + Pos(20, 20), state.metrics, {
			+ActionItem("Sub Normal", downAlongLeftMargin(), state.metrics, {
			})
			+ActionItem("Sub Disabled", downAlongLeftMargin(), state.metrics, {
				disabled = true
			})
			+ActionItem("Sub Checkbox value", downAlongLeftMargin(), state.metrics, {
				checkBoxValue = booleanValue
			})
			+Textfield(strValue, 10, downAlongLeftMargin(), state.metrics, {
			})
			+Button("Sub Start", downAlongLeftMargin(), state.metrics, {
				width = 200
			})
		})
		subMenu.handleEvents(state)
		val showSubActionMenu = parentActionItem!!.hover || subMenu.hover
		if (showSubActionMenu) {
			subMenu.draw(app.skin)
		}
		if (state.leftMouseButton.just_released) {
			showActionMenu = contextMenu.hover || showSubActionMenu
		}
	}
	debugLines.add("h: ${state.isDown('h')}")
	debugLines.add("mousePos: ${state.mousePos.x}, ${state.mousePos.y}")
	debugLines.add("mouseScrollDelta: ${state.mouseScrollDelta}")

	if (state.isJustReleased('h')) {
		showDebugLines = !showDebugLines
	}
}

/*val app2: Application = object : Application(DiscoverUI(1397, 796, 3)) {
	override fun doFrame() {
		demoAppLogic(this)
	}
}
fun main(args: Array<String>) {

}
*/



/* Javascript hívási példa!!
JS-be elég egy key nevű metódus
native("key")
fun callJavascript() {}
*/


fun tabPanel(metrics: AppSizeMetricData, state: AppState): TabPanel {
	val tabPanel = TabPanel(tabPanelValue, Pos(50, 20), metrics,  {
		width = 1000
		height = 600
		addTabPanelItem("Buttons")
		addTabPanelItem("Textfields")
		addTabPanelItem("Checkboxes", { variant = Variant.SUCCESS })
		addTabPanelItem("Radioboxes")
		addTabPanelItem("Graph", { variant = Variant.DANGER })
		addTabPanelItem("Disabled", { disabled = true })
		if (tabPanelValue.value == 0) {
			+Panel(downAlongLeftMargin(), state.metrics, {
				+Button("Default Button", downAlongLeftMargin(), state.metrics, {
					width = 200
					variant = Variant.DEFAULT
					onClick = {
						strValue.value = strValue.value + "Default"
					}
				})
				+Button("Green Button", downAlongLeftMargin(20),metrics, {
					width = 200
					variant = Variant.SUCCESS
					onClick = {
						strValue.value = strValue.value + "Green"
					}
				})
				+Button("Red Button", downAlongLeftMargin(20), metrics, {
					width = 200
					variant = Variant.DANGER
				})
				+Button("Yellow Button", downAlongLeftMargin(20), metrics, {
					width = 200
					variant = Variant.WARNING
				})
				+Button("Info Button", downAlongLeftMargin(20), metrics, {
					width = 200
					variant = Variant.INFO
				})
				+Button("Button".allocNew(), downAlongLeftMargin(20), metrics, {
					width = 200
					variant = Variant.WARNING
				})
				+Button("Button".allocNew(), downAlongLeftMargin(20), metrics, {
					width = 200
					variant = Variant.INFO
				})
				+Button("Inactive Button", downAlongLeftMargin(20), metrics, {
					width = 200
					disabled = true
				})
			})
		} else if (tabPanelValue.value == 1) {
			+Panel(downAlongLeftMargin(), state.metrics, {
				+Textfield(strValue, 10, downAlongLeftMargin(10), state.metrics, {
					variant = Variant.DEFAULT
				})
				+Textfield(strValue1, 10, downAlongLeftMargin(10), state.metrics, {
					variant = Variant.INFO
				})
				+Textfield(strValue2, 10, downAlongLeftMargin(), metrics, {
					variant = Variant.WARNING
				})
				+Textfield(strValue3, 10, downAlongLeftMargin(20), metrics, {
					variant = Variant.DANGER
				})
				+Textfield(strValue4, 10, downAlongLeftMargin(20), metrics, {
					variant = Variant.SUCCESS
				})
				+Textfield(strValue5, 10, downAlongLeftMargin(20), metrics, {
					disabled = true
				})
			})
		} else if (tabPanelValue.value == 2) {
			+Panel(downAlongLeftMargin(), state.metrics, {
				+Checkbox("Default", booleanValues[0], downAlongLeftMargin(10), metrics)
				+Checkbox("Info", booleanValues[1], downAlongLeftMargin(10), state.metrics, {
					variant = Variant.INFO
				})
				+Checkbox("Warning", booleanValues[2], downAlongLeftMargin(10), state.metrics, {
					variant = Variant.WARNING
				})
				+Checkbox("Error", booleanValues[3], downAlongLeftMargin(10), state.metrics, {
					variant = Variant.DANGER
				})
				+Checkbox("Success", booleanValues[4], downAlongLeftMargin(10), state.metrics, {
					variant = Variant.SUCCESS
				})
				+Checkbox("Disabled", booleanValues[0], downAlongLeftMargin(10), state.metrics, {
					disabled = true
				})
			})
		} else if (tabPanelValue.value == 3) {
			+Panel(downAlongLeftMargin(), state.metrics, {
				+RadioButton("Default", radioButtonValue, 0, downAlongLeftMargin(10), metrics)
				+RadioButton("Info", radioButtonValue, 1, downAlongLeftMargin(10), state.metrics, {
					variant = Variant.INFO
				})
				+RadioButton("Warning", radioButtonValue, 2, downAlongLeftMargin(10), state.metrics, {
					variant = Variant.WARNING
				})
				+RadioButton("Danger", radioButtonValue, 3, downAlongLeftMargin(10), state.metrics, {
					variant = Variant.DANGER
				})
				+RadioButton("Success", radioButtonValue, 4, downAlongLeftMargin(10), state.metrics, {
					variant = Variant.SUCCESS
				})
				+RadioButton("Disabled", radioButtonValue, 5, downAlongLeftMargin(10), state.metrics, {
					disabled = true
				})
			})
		} else if (tabPanelValue.value == 4) {
			+Timeline(downAlongLeftMargin(), 500, 500, state.metrics, {
				for ( (i, v) in booleanValues.zip(showAvgValues).withIndices()) {
					val (showGraph, showAvg) = v
					if (i == 0 && showGraph.value) {
						+FloatsAndSinkersChart(graphData[i], graphAvgData[i], {
							actualDataColor = "#29A1D3"
							trendColor = "#EE4E10"
						})
					} else if (i == 1 && showGraph.value) {
						+FloatsAndSinkersChart(graphData[i], graphAvgData[i], {
							actualDataColor = "#29A1D3"
							trendColor = "#EE4E10"
						})
					} else if (showGraph.value) {
						+LineChart(graphData[i], {
							color = when (Variant.values()[i]) {
								Variant.INFO -> "#29A1D3"
								Variant.DEFAULT -> "#525864"
								Variant.SUCCESS -> "#8AB71C"
								Variant.WARNING -> "#F1B018"
								Variant.DANGER -> "#EE4E10"
							}
						})
						if (showAvg.value) {
							+LineChart(graphAvgData[i], {
								color = when (Variant.values()[i]) {
									Variant.DANGER -> "#58B4DB"
									Variant.WARNING -> "#6B6B6B"
									Variant.INFO -> "#9BBC45"
									Variant.DEFAULT -> "#F7C44C"
									Variant.SUCCESS -> "#F47344"
								}
							})
						}
					}
				}
			})
		}
	})
	tabPanel.drawAndHandleEvents(state, app.skin)
	return tabPanel
}