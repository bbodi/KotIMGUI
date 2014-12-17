package timeline

object EventItemFactory {
	fun getEventItem(type: EventType, date: Date, value: Any?): EventItem {
		val param: Ptr<Float>? = value as? Ptr<Float>?
		return NumberEventItem(param ?: Ptr(0f), date, "")
		/*when (type) {
			EventType.NUMBER ->

		}*/
	}
}