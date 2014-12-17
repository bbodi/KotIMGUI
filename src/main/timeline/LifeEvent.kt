package timeline

import kotlin.js
import java.io.Serializable
import widget.WidgetContainer

enum class EventType {
	NUMBER
	OCCURRED_OR_NOT
	RANGE
	TEXT
	IMAGE
	EVENT_LINK
}

class EventTemplate(name: String, type: EventType = EventType.NUMBER) {
	val typePtr = Ptr(type)
	val name = Ptr(name)
}

abstract class EventItem(val date: Date, val comment: String, val type: EventType) {
	abstract fun getListingWidgets(parent: WidgetContainer, state: AppState)
}