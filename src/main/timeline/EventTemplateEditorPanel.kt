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
import widget.TabPanel
import widget.WidgetContainer


class EventTemplatePanel(val metrics: AppSizeMetricData) {

	fun drawEventTemplatePanel(eventTemplates: MutableList<EventTemplate>,
							   appState: AppState,
							   parent: WidgetContainer): Boolean {
		var isTemplateChanged = false
		parent + {
			eventTemplates.forEach { template ->
				+Label("Template name: ", downAlongLeftMargin(), metrics)
				+Textfield(template.name, (template.name.value.length+1).atLeast(10), toRightFromLastWidget(10), metrics, {
					variant = if (template.name.value.length == 0) Variant.DANGER else Variant.DEFAULT
					onChange = {
						isTemplateChanged = true
					}
				})
				val fieldType = IntValue(template.type.ordinal())
				+NumberField(fieldType, 10, toRightFromLastWidget(10), metrics, {
					valueLabels = EventType.values().map { it.toString() }.copyToArray()
					onChange = {
						template.type = EventType.values()[fieldType.value]
						isTemplateChanged = true
					}
				})
				+Button("Delete", toRightFromLastWidget(10), metrics, {
					width = 200
					onClick = {

					}
				})
			}
			+Button("CreateNew", downAlongLeftMargin(), metrics, {
				width = 200
				onClick = {
					createNewTemplate(eventTemplates)
					isTemplateChanged = true
				}
			})
		}

		return isTemplateChanged
	}

	private fun createNewTemplate(eventTemplates: MutableList<EventTemplate>) {
		eventTemplates.add(EventTemplate("Unnamed"))
	}
}