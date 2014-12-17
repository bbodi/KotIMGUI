package widget

import kotlin.test.assertEquals
import org.junit.Test;
import widget.Button
import widget.NumberField
import widget.Pos
import timeline.AppState
import timeline.AppSizeMetricData
import skin.Font
import widget.Textfield
import timeline.Keys
import timeline.Ptr

public class TabPanelTest {

	Test
	fun testTabPanelSizeWithoutContent() {
		val tabIndex = Ptr(0)
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)

		val tabPanel = TabPanel(tabIndex, Pos(100, 100), metrics, {
			addTabPanelItem("1")
			addTabPanelItem("2")
			addTabPanelItem("3")
		})
		val tabButtonWidth = (1+2) * metrics.charWidth
		assertEquals(tabButtonWidth*3, tabPanel.width)
		assertEquals(metrics.rowHeight + metrics.panelBorder*2, tabPanel.height)
		assertEquals(100 + metrics.panelBorder, tabPanel.contentX)
		assertEquals(100 + metrics.rowHeight+metrics.panelBorder, tabPanel.contentY)
		assertEquals((tabButtonWidth*3) - (2*metrics.panelBorder), tabPanel.contentWidth)
		assertEquals(0, tabPanel.contentHeight)
	}

	Test
	fun testTabPanelWithButtonContent() {
		val tabIndex = Ptr(0)
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)

		val tabPanel = TabPanel(tabIndex, Pos(100, 100), metrics, {
			addTabPanelItem("1")
			addTabPanelItem("2")
			addTabPanelItem("3")
			+Button("Ok", downAlongLeftMargin(), metrics)
		})
		val tabButtonWidth = (1+2) * metrics.charWidth
		assertEquals(tabButtonWidth*3, tabPanel.width)
		assertEquals(metrics.rowHeight*2 + (2*metrics.panelBorder), tabPanel.height)
		assertEquals(100 + metrics.panelBorder, tabPanel.contentX)
		assertEquals(100 + metrics.rowHeight + metrics.panelBorder, tabPanel.contentY)
		assertEquals((tabButtonWidth*3) - (2*metrics.panelBorder), tabPanel.contentWidth)
		assertEquals(metrics.rowHeight, tabPanel.contentHeight)
	}

}
