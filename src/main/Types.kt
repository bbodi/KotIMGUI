package timeline;

public class Ptr<T>(var value: T)

fun newDate(year: Int, month: Int, day: Int): Date {
	val date = Date()
	date.setTime(0)
	date.setYear(year)
	date.setMonth(month)
	date.setDayOfMonth(day)
	return date
	// ms: Int = js.Date().getTime()
}

fun newDateForAbsoluteDay(day: Int): Date {
	val date = Date()
	date.setTime(day * 24 * 60 * 60 * 1000)
	return date
}

public fun Date.getDayOfMonth(): Int = this.getDate() - 1
public fun Date.setDayOfMonth(param: Int): Unit {
	this.setDate(param+1)
}

native
public class Date() {
	public fun getTime(): Int = noImpl

	native("getDate")
	public fun getDate(): Int = noImpl

	native("getDay")
	public fun getDayOfWeek(): Int = noImpl

	native("getFullYear")
	public fun getYear(): Int = noImpl
	public fun getHours(): Int = noImpl
	public fun getMilliseconds(): Int = noImpl
	public fun getMinutes(): Int = noImpl
	public fun getMonth(): Int = noImpl
	public fun getSeconds(): Int = noImpl

	native("setDate")
	public fun setDate(param: Int): Unit= noImpl

	native("setDay")
	public fun setDayOfWeek(param: Int): Unit= noImpl

	public fun setTime(param: Int): Unit= noImpl

	native("setFullYear")
	public fun setYear(param: Int): Unit= noImpl
	public fun setHours(param: Int): Unit= noImpl
	public fun setMilliseconds(param: Int): Unit= noImpl
	public fun setMinutes(param: Int): Unit= noImpl
	public fun setMonth(param: Int): Unit= noImpl
	public fun setSeconds(param: Int): Unit= noImpl

	override fun toString(): String = "${getYear()}.${getMonth()}.${getDayOfMonth()}"
}

public fun Int.atMost(max: Int): Int {
	return Math.min(this, max)
}

public fun Int.atLeast(min: Int): Int {
	return Math.max(this, min)
}

public fun Int.limit_into(min: Int, max: Int): Int {
	return this.atLeast(min).atMost(max)
}