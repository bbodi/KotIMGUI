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
import kotlin.test.assertTrue
import timeline.Ptr

public class TextfieldTest {

	Test
	fun testBackspace() {
		val text = Ptr("asd")
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)

		val textfield = Textfield(text, 10, Pos(10, 10), metrics)
		assertEquals(3, textfield.cursorPos)
		focus(textfield, state)
		state.updateKey(Keys.Backspace, true)
		textfield.handleEvents(state)
		assertEquals("as", text.value)
		assertEquals(2, textfield.cursorPos)
	}

	Test
	fun testFocus() {
		val text = Ptr("asd")
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)

		val textfield = Textfield(text, 10, Pos(10, 10), metrics)
		focus(textfield, state)
		textfield.handleEvents(state)
		assertTrue(textfield.isActive)
		textfield.handleEvents(state)
		assertTrue(textfield.isActive)
		textfield.handleEvents(state)
		assertTrue(textfield.isActive)
	}

	Test
	fun testCtrlBackspace() {
		val text = Ptr("asd")
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)

		val textfield = Textfield(text, 10, Pos(10, 10), metrics)
		assertEquals(3, textfield.cursorPos)
		focus(textfield, state)
		state.updateKey(Keys.Ctrl, true)
		state.updateKey(Keys.Backspace, true)
		textfield.handleEvents(state)
		assertEquals("", text.value)
		assertEquals(0, textfield.cursorPos)
	}

	Test
	fun testDelete() {
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)

		val text = Ptr("asd")
		val textfield = Textfield(text, 10, Pos(10, 10), metrics, {
			cursorPos = 0
		})
		assertEquals(0, textfield.cursorPos)
		focus(textfield, state)
		state.updateKey(Keys.Del, true)
		textfield.handleEvents(state)
		assertEquals("sd", text.value)
		assertEquals(0, textfield.cursorPos)
	}

	Test
	fun testCtrlDelete() {
		val text = Ptr("asd")
		val metrics = AppSizeMetricData(Font(16, "Courier New"), rowHeight = 20, textMarginY = 5, charWidth = 10, charHeight = 10, panelBorder = 5)
		val state = AppState(metrics)

		val textfield = Textfield(text, 10, Pos(10, 10), metrics, {
			cursorPos = 0
		})
		assertEquals(0, textfield.cursorPos)
		focus(textfield, state)
		state.updateKey(Keys.Ctrl, true)
		state.updateKey(Keys.Del, true)
		textfield.handleEvents(state)
		assertEquals("", text.value)
		assertEquals(0, textfield.cursorPos)
	}

	private fun focus(field: Textfield, state: AppState) {
		state.active_widget_id = field.id
	}

}