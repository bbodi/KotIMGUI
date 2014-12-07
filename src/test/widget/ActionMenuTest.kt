import kotlin.test.assertEquals
import org.junit.Test;
import widget.Button
import widget.NumberField
import timeline.IntValue
import widget.Pos
import timeline.AppState
import timeline.AppSizeMetricData
import skin.Font
import widget.ActionMenu
import widget.ActionItem
import widget.Widget
import kotlin.properties.Delegates

public class ActionMenuTest {

	Test
	fun testActionItemHover() {
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)
		val actionMenu = ActionMenu(Pos(10, 10), metrics, {
			+ActionItem("First", downAlongLeftMargin(), metrics)
			val secondItem = ActionItem("Second", downAlongLeftMargin(10), metrics)
			+secondItem
			moveMouseToSecondItem(secondItem, metrics, state)
			+ActionItem("Third", downAlongLeftMargin(10), metrics)
		})
		state.leftMouseButton.update(true)
		actionMenu.handleEvents(state)
		assertEquals(false, (actionMenu.widgets[0] as ActionItem).highlight )
		assertEquals(true, (actionMenu.widgets[1] as ActionItem).highlight )
		assertEquals(false, (actionMenu.widgets[2] as ActionItem).highlight )
	}


	private fun moveMouseToSecondItem(widget: Widget, metrics: AppSizeMetricData, state: AppState) {
		val upperButtonPos = widget.pos + Pos(widget.width - (metrics.charWidth * 3), 0)
		state.mousePos = upperButtonPos
	}

}