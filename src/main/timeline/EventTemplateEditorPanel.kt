package timeline

import widget.ActionMenu
import widget.ActionItem
import widget.Textfield
import widget.Button
import widget.Pos
import widget.Panel
import skin.Variant
import widget.NumberField
import widget.Label
import skin.Skin


class EventTemplatePanel(val metrics: AppSizeMetricData) {
	private var selectedItemIndex: Int? = null

	fun drawEventTemplatePanel(pos: Pos,
							   eventTemplates: MutableList<EventTemplate>,
							   appEvent: AppState,
							   skin: Skin) {

		val contextMenu = ActionMenu(pos, metrics, {
			eventTemplates.withIndices().forEach {
				+ActionItem(downUnderMargin(), metrics, {
					val (index, template) = it
					label = template.name.value
					onClick = {selectedItemIndex = index}
					highlight = selectedItemIndex == index
				})
			}
			+Button("CreateNew", downAlongLeftMargin(), metrics, {
				width = 200
				onClick = {createNewTemplate(eventTemplates)}
			})
		})
		contextMenu.drawAndHandleEvents(appEvent, skin)

		if (selectedItemIndex != null) {
			drawItemPanel(contextMenu.toRight(10), eventTemplates[selectedItemIndex!!], appEvent, skin);
		}
	}

	private fun createNewTemplate(eventTemplates: MutableList<EventTemplate>) {
		eventTemplates.add(EventTemplate(""))
	}

	private fun drawItemPanel(pos: Pos,
							  eventTemplate: EventTemplate,
							  appEvent: timeline.AppState,
							  skin: Skin) {
		Panel(pos, metrics, {
			additionalIdInfo = "SelectedTemplatePanel"
			+Label("Template name: ", downAlongLeftMargin(), metrics)
			+Textfield(eventTemplate.name, 10, toRightFromLastWidget(10), metrics, {
				variant = if (eventTemplate.name.value.length == 0) Variant.DANGER else Variant.DEFAULT
			})

			+Panel(downAlongLeftMargin(20), metrics, {
				eventTemplate.fields.withIndices().forEach {
					val (index, field) = it
					val fieldType = IntValue(field.type.ordinal())
					+Label("Field name: ", downAlongLeftMargin(), metrics)
					+Textfield(field.name, 10, toRightFromLastWidget(10), metrics, {
						variant = if (field.name.value.length == 0) Variant.DANGER else Variant.DEFAULT
					})
					+NumberField(fieldType, 10, toRightFromLastWidget(10), metrics, {
						additionalIdInfo = "$index"
						valueLabels = EventFieldType.values().map { it.toString() }.copyToArray()
						onChange = {
							field.type = EventFieldType.values()[fieldType.value]
						}
					})
				}
				+Button("CreateNew", downAlongLeftMargin(), metrics, {
					width = 200
					onClick = {eventTemplate.fields.add(EventField())}
				})
			})
		}).drawAndHandleEvents(appEvent, skin)
	}
}