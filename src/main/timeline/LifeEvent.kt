package timeline

import kotlin.js
import java.io.Serializable

enum class EventType {
	NUMBER
	OCCURRED_OR_NOT
	RANGE
	TEXT
	IMAGE
	EVENT_LINK
}

class EventTemplate(name: String, var type: EventType = EventType.NUMBER) {
	val name = StrValue(name)
}

class Event(val date: js.Date, val comment: String): Serializable {
	//val fields: EventType = arrayListOf<>()
}