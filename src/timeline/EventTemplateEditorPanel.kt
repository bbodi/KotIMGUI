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


class EventTemplatePanel {
	private var selectedItemIndex: Int? = null
	fun drawEventTemplatePanel(pos: Pos, eventTemplates: MutableList<EventTemplate>) {

		val contextMenu = ActionMenu(pos, {
			eventTemplates.withIndices().forEach {
				+ActionItem(downUnderMargin(), {
					val (index, template) = it
					label = template.name.value
					onClick = {selectedItemIndex = index}
					highlight = selectedItemIndex == index
				})
			}
			+Button("CreateNew", downAlongLeftMargin(), {
				width = 200
				onClick = {createNewTemplate(eventTemplates)}
			})
		})
		contextMenu.drawAndHandleEvents()

		if (selectedItemIndex != null) {
			drawItemPanel(contextMenu.toRight(10), eventTemplates[selectedItemIndex!!]);
		}
	}

	private fun createNewTemplate(eventTemplates: MutableList<EventTemplate>) {
		eventTemplates.add(EventTemplate(""))
	}

	private fun drawItemPanel(pos: Pos, eventTemplate: EventTemplate) {
		Panel(pos, {
			additionalIdInfo = "SelectedTemplatePanel"
			+Label("Template name: ", downAlongLeftMargin())
			+Textfield(eventTemplate.name, 10, toRightFromLastWidget(10), {
				variant = if (eventTemplate.name.value.length == 0) Variant.DANGER else Variant.DEFAULT
			})

			+Panel(downAlongLeftMargin(20), {
				eventTemplate.fields.withIndices().forEach {
					val (index, field) = it
					val fieldType = IntValue(field.type.ordinal())
					+Label("Field name: ", downAlongLeftMargin())
					+Textfield(field.name, 10, toRightFromLastWidget(10), {
						variant = if (field.name.value.length == 0) Variant.DANGER else Variant.DEFAULT
					})
					+NumberField(fieldType, 10, toRightFromLastWidget(10), {
						additionalIdInfo = "$index"
						valueLabels = EventFieldType.values().map { it.toString() }.copyToArray()
						onChange = {
							field.type = EventFieldType.values()[fieldType.value]
						}
					})
				}
				+Button("CreateNew", downAlongLeftMargin(), {
					width = 200
					onClick = {eventTemplate.fields.add(EventField())}
				})
			})
		}).drawAndHandleEvents()
	}
}