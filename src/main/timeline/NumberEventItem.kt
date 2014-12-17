package timeline

import widget.WidgetContainer
import widget.Label
import widget.Textfield
import skin.Variant
import widget.NumberField
import widget.FloatNumberField
import widget.Checkbox

class NumberEventItem(val ptr: Ptr<Float>, date: Date, comment: String) : EventItem(date, comment, EventType.NUMBER) {

	override fun getListingWidgets(parent: WidgetContainer, state: AppState) {
		parent + {
			+FloatNumberField(ptr, ptr.value.toString().length.atLeast(5), toRightFromLastWidget(10), metrics, {
				//variant = if (template.name.value.length == 0) Variant.DANGER else Variant.DEFAULT
				onChange = {

				}
			})
		}
	}
}