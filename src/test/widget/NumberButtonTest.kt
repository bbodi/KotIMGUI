import kotlin.test.assertEquals
import org.junit.Test;
import widget.Button
import widget.IntNumberField
import widget.Pos
import timeline.AppState
import timeline.AppSizeMetricData
import skin.Font
import kotlin.test.assertTrue
import timeline.Ptr
import widget.IntNumberField

public class NumberButtonTest {
	Test
	fun testUpperButtonDown() {
		val value = Ptr(2)
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)
		assertEquals(2, value.value)
		val field = IntNumberField(value, 10, Pos(10, 10), metrics, {
		})
		moveMouseToUpperButton(field, metrics, state)
		click(field, state)
		assertEquals(3, value.value)
	}

	Test
	fun testLowerButtonDown() {
		val value = Ptr(2)
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)
		assertEquals(2, value.value)
		val field = IntNumberField(value, 10, Pos(10, 10), metrics, {
		})
		moveMouseToLowerButton(field, metrics, state)
		click(field, state)
		assertEquals(1, value.value)
	}

	Test
	fun testButtonPressingWhenMouseIsReleased() {
		val value = Ptr(2)
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)
		assertEquals(2, value.value)
		val field = IntNumberField(value, 10, Pos(10, 10), metrics, {
		})
		moveMouseToUpperButton(field, metrics, state)
		state.leftMouseButton.update(true)
		handleMouseDownState(field, state)
		state.leftMouseButton.update(false)
		field.handleEvents(state)
		assertEquals(3, value.value)

		click(field, state)
		assertEquals(4, value.value, "Between clicks, there is no delay penalty")
	}

	Test
	fun testButtonPressingWhenMouseIsNotReleasedButDown() {
		val value = Ptr(2)
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)
		assertEquals(2, value.value)
		val field = IntNumberField(value, 10, Pos(10, 10), metrics, {
		})
		moveMouseToUpperButton(field, metrics, state)
		state.leftMouseButton.update(true)
		field.handleEvents(state)
		assertEquals(2, value.value)

		field.handleEvents(state)
		assertEquals(2, value.value, "Currently, NumberFields cannot be modified by holding down the up/down buttons!")

		state.currentTick += 200 + 800 + 1
		field.handleEvents(state)
		assertEquals(2, value.value, "Currently, NumberFields cannot be modified by holding down the up/down buttons!")

		state.leftMouseButton.update(false)
		field.handleEvents(state)
		assertEquals(3, value.value)
	}

	Test
	fun testTextChangesAsValueChanges() {
		val value = Ptr(2)
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)
		assertEquals(2, value.value)
		val field = IntNumberField(value, 10, Pos(10, 10), metrics, {
		})
		moveMouseToUpperButton(field, metrics, state)
		click(field, state)
		assertEquals(3, value.value)
		assertEquals("3", field.textPtr.value, "The value increased, so the text must be changed accordingly too!")
	}

	Test
	fun testFocus() {
		val value = Ptr(2)
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)
		val field = IntNumberField(value, 10, Pos(10, 10), metrics, {
		})
		focus(field, state)
		assertTrue(field.isActive)
		field.handleEvents(state)
		assertTrue(field.isActive, "It should keep the focus!")
	}

	private fun click(field: IntNumberField, state: AppState) {
		state.leftMouseButton.update(true)
		field.handleEvents(state)
		state.leftMouseButton.update(false)
		field.handleEvents(state)
	}

	private fun focus(field: IntNumberField, state: AppState) {
		state.mousePos = field.pos + Pos(1, 1)
		click(field, state)
	}

	private fun handleMouseDownState(field: IntNumberField, state: AppState) {
		field.handleEvents(state)
	}

	private fun moveMouseToUpperButton(field: IntNumberField, metrics: AppSizeMetricData, state: AppState) {
		val upperButtonPos = field.pos + Pos(field.width - (metrics.charWidth * 3), 0)
		state.mousePos = upperButtonPos
	}

	private fun moveMouseToLowerButton(field: IntNumberField, metrics: AppSizeMetricData, state: AppState) {
		val upperButtonPos = field.pos + Pos(field.width - (metrics.charWidth * 3), metrics.rowHeight / 2)
		state.mousePos = upperButtonPos
	}
}