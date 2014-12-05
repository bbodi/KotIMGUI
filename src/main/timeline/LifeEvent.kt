package timeline

import kotlin.js

enum class EventFieldType {
	NUMBER
	OCCURRED_OR_NOT
	RANGE
	TEXT
	IMAGE
	EVENT_LINK
}

class EventField() {
	var type: EventFieldType = EventFieldType.NUMBER
	var name: StrValue = StrValue("")
}

class EventTemplate(name: String) {
	val name = StrValue(name)
	val fields: MutableList<EventField> = arrayListOf()
}

class Event(val date: js.Date, val comment: String) {
	val fields: MutableList<EventField> = arrayListOf()
}