package timeline;

public class IntValue(var value: Int)
public class FloatValue(var value: Float)
public class StrValue(var value: String)
public class BooleanValue(var value: Boolean)
native
public class Date(ms: Int) {
	public fun getTime(): Int = noImpl
	native("getDate")
	public fun getDayOfMonth(): Int = noImpl

	native("getDay")
	public fun getDayOfWeek(): Int = noImpl

	public fun getFullYear(): Int = noImpl
	public fun getHours(): Int = noImpl
	public fun getMilliseconds(): Int = noImpl
	public fun getMinutes(): Int = noImpl
	public fun getMonth(): Int = noImpl
	public fun getSeconds(): Int = noImpl
}

public fun Int.at_most(max: Int): Int {
	return Math.min(this, max)
}

public fun Int.atLeast(min: Int): Int {
	return Math.max(this, min)
}

public fun Int.limit_into(min: Int, max: Int): Int {
	return this.atLeast(min).at_most(max)
}