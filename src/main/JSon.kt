package timeline

import java.io.IOException
import java.util.LinkedHashMap
import java.util.LinkedList
import kotlin.js
import kotlin.js

public fun json(init: Json.() -> Unit): Json {
	val obj = Json(JsonType.OBJECT, hashMapOf<String, Json>())
	obj.init()
	return obj
}

public enum class JsonType {
	STRING
	NUMBER
	BOOLEAN
	OBJECT
	ARRAY
	BEGIN
	NULL
	STRUCT
}

public class Json(val type: JsonType, val value: Any?) {

	fun get(key: String): Json? {
		return getObject()[key]
	}

	fun String.rangeTo(value: String) {
		val m = getObject()
		m[this] = Json(JsonType.STRING, value)
	}

	fun String.rangeTo(value: Int) {
		val m = getObject()
		m[this] = Json(JsonType.NUMBER, value)
	}

	fun String.rangeTo(func: Json.()->Unit) {
		val m = getObject()
		val obj = Json(JsonType.OBJECT, hashMapOf<String, Json>())
		obj.func()
		m[this] = obj
	}


	fun String.rangeTo(value: Array<Json>) {
		val m = getObject()
		m[this] = Json(JsonType.ARRAY, value)
	}

	override fun toString(): String {
		val sb = StringBuilder()
		when (this.type) {
			JsonType.ARRAY -> {
				sb.append('[')
				var first = true
				for (json in getArray()) {
					if (!first)
						sb.append(',')
					else
						first = false
					sb.append(json.toString())
				}
				sb.append(']')
				return sb.toString()
			}
			JsonType.OBJECT -> {
				sb.append('{')
				var first = true
				val m = getObject()
				for ((key, value) in m) {
					if (!first)
						sb.append(',')
					else
						first = false
					sb.append("\"$key\":${value.toString()}")
				}
				sb.append('}')
				return sb.toString()
			}
			JsonType.STRING -> return "\"$value\""
			JsonType.NUMBER -> return "\"$value\""
			JsonType.NULL -> return "null"
			JsonType.BOOLEAN -> return "\"$value\""
		}
		throw IllegalStateException()
	}

	public fun getArray(): Array<Json> {
		if (this.type == JsonType.ARRAY) {
			return (value as Array<Json>)
		}
		throw UnsupportedOperationException("")
	}

	public fun getObject(): MutableMap<String, Json> {
		if (this.type == JsonType.OBJECT) {
			return value as MutableMap<String, Json>
		}
		throw UnsupportedOperationException("")
	}

	fun getString(): String {
		return value as String
	}
}