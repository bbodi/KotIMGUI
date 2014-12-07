package timeline
import java.io.IOException
import timeline.JsonType
import timeline.Json


/*
   * |                      bufferOffset
   *                        v
   * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t]        < input
   *                       [l|m|n|o|p|q|r|s|t|?|?]    < buffer
   *                          ^               ^
   *                       |  index           fill
   */

class ParseException(val msg: String, val offset:Int, val line: Int, val column: Int): RuntimeException(msg)

public class JsonParser(private val string: String) {
	private val buffer: Array<Char> = string.toArrayList().copyToArray()
	private var bufferOffset: Int = 0
	private var index: Int = 0
	private var line: Int = 0
	private var lineOffset: Int = 0
	private var current: Int = 0
	private var captureBuffer: StringBuilder = StringBuilder()
	private var captureStart: Int = 0

	{
		line = 1
		captureStart = -1
	}

	fun parse(): Json {
		read()
		skipWhiteSpace()
		val result = readValue()
		skipWhiteSpace()
		if (!isEndOfText()) {
			throw error("Unexpected character")
		}
		return result
	}

	
	private fun readValue(): Json {
		when (current.toChar()) {
			'n' -> return readNull()
			't' -> return readTrue()
			'f' -> return readFalse()
			'"' -> return readString()
			'[' -> return readArray()
			'{' -> return readObject()
			'-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> return readNumber()
			else -> throw expected("value")
		}
	}

	private fun readArray(): Json {
		read()
		val list = arrayListOf<Json>()
		skipWhiteSpace()
		if (readChar(']')) {
			return Json(JsonType.ARRAY, list.copyToArray())
		}
		do {
			skipWhiteSpace()
			list.add(readValue())
			skipWhiteSpace()
		} while (readChar(','))
		if (!readChar(']')) {
			throw expected("',' or ']'")
		}
		return Json(JsonType.ARRAY, list.copyToArray())
	}

	private fun readObject(): Json {
		read()
		val map: MutableMap<String, Json> = hashMapOf()
		skipWhiteSpace()
		if (readChar('}')) {
			return Json(JsonType.OBJECT, map)
		}
		do {
			skipWhiteSpace()
			val name = readName()
			skipWhiteSpace()
			if (!readChar(':')) {
				throw expected("':'")
			}
			skipWhiteSpace()
			val value = readValue()
			map[name] = value
			skipWhiteSpace()
		} while (readChar(','))
		if (!readChar('}')) {
			throw expected("',' or '}'")
		}
		return Json(JsonType.OBJECT, map)
	}

	private fun readName(): String {
		if (current.toChar() != '"') {
			throw expected("name")
		}
		return readStringInternal()
	}

	private fun readNull(): Json {
		read()
		readRequiredChar('u')
		readRequiredChar('l')
		readRequiredChar('l')
		return Json(JsonType.NULL, null)
	}

	private fun readTrue(): Json {
		read()
		readRequiredChar('r')
		readRequiredChar('u')
		readRequiredChar('e')
		return Json(JsonType.BOOLEAN, true)
	}

	private fun readFalse(): Json {
		read()
		readRequiredChar('a')
		readRequiredChar('l')
		readRequiredChar('s')
		readRequiredChar('e')
		return Json(JsonType.BOOLEAN, false)
	}

	private fun readRequiredChar(ch: Char) {
		if (!readChar(ch)) {
			throw expected("'" + ch + "'")
		}
	}

	private fun readString(): Json {
		return Json(JsonType.STRING, readStringInternal())
	}

	private fun readStringInternal(): String {
		read()
		startCapture()
		while (current.toChar() != '"') {
			if (current.toChar() == '\\') {
				pauseCapture()
				readEscape()
				startCapture()
			} else if (current < 32) {
				throw expected("valid string character")
			} else {
				read()
			}
		}
		val string = endCapture()
		read()
		return string
	}

	private fun readEscape() {
		read()
		when (current.toChar()) {
			'"', '/', '\\' -> captureBuffer.append(current.toChar())
			'b' -> captureBuffer.append('\b')
			//'f' -> captureBuffer!!.append('\f')
			'n' -> captureBuffer.append('\n')
			'r' -> captureBuffer.append('\r')
			't' -> captureBuffer.append('\t')
			'u' -> {
				val hexChars = CharArray(4)
				for (i in 0..4 - 1) {
					read()
					if (!isHexDigit()) {
						throw expected("hexadecimal digit")
					}
					hexChars[i] = current.toChar()
				}
				captureBuffer.append(parseInt(hexChars.joinToString(separator=""), 16).toChar())
			}
			else -> throw expected("valid escape sequence")
		}
		read()
	}

	private fun readNumber(): Json {
		startCapture()
		readChar('-')
		val firstDigit = current.toChar()
		if (!readDigit()) {
			throw expected("digit")
		}
		if (firstDigit != '0') {
			while (readDigit()) {
			}
		}
		readFraction()
		readExponent()
		return Json(JsonType.NUMBER, endCapture())
	}

	private fun readFraction(): Boolean {
		if (!readChar('.')) {
			return false
		}
		if (!readDigit()) {
			throw expected("digit")
		}
		while (readDigit()) {
		}
		return true
	}

	private fun readExponent(): Boolean {
		if (!readChar('e') && !readChar('E')) {
			return false
		}
		if (!readChar('+')) {
			readChar('-')
		}
		if (!readDigit()) {
			throw expected("digit")
		}
		while (readDigit()) {
		}
		return true
	}

	private fun readChar(ch: Char): Boolean {
		if (current.toChar() != ch) {
			return false
		}
		read()
		return true
	}

	private fun readDigit(): Boolean {
		if (!isDigit()) {
			return false
		}
		read()
		return true
	}

	private fun skipWhiteSpace() {
		while (isWhiteSpace()) {
			read()
		}
	}

	private fun read() {
		if (isEndOfText()) {
			return
		}

		if (current.toChar() == '\n') {
			line++
			lineOffset = bufferOffset + index
		}
		current = buffer[index++].toInt()
	}

	private fun startCapture() {
		captureStart = index - 1
	}

	private fun pauseCapture() {
		val end = if (current == -1) index else index - 1
		captureBuffer.append(buffer, captureStart, end - captureStart)
		captureStart = -1
	}

	private fun endCapture(): String {
		val end = if (current == -1) index else index - 1
		val captured: String
		if (captureBuffer.toString().length() > 0) {
			captureBuffer.append(buffer, captureStart, end - captureStart)
			captured = captureBuffer.toString()
			captureBuffer = StringBuilder()
		} else {
			captured = buffer.drop(captureStart).take(end - captureStart).joinToString(separator="")
		}
		captureStart = -1
		return captured
	}

	private fun expected(expected: String): ParseException {
		if (isEndOfText()) {
			return error("Unexpected end of input")
		}
		return error("Expected " + expected)
	}

	private fun error(message: String): ParseException {
		val absIndex = bufferOffset + index
		val column = absIndex - lineOffset
		val offset = if (isEndOfText()) absIndex else absIndex - 1
		return ParseException(message, offset, line, column - 1)
	}

	private fun isWhiteSpace(): Boolean {
		return current.toChar() == ' ' || current.toChar() == '\t' || current.toChar() == '\n' || current.toChar() == '\r'
	}

	private fun isDigit(): Boolean {
		return current >= '0' && current <= '9'
	}

	private fun isHexDigit(): Boolean {
		return current >= '0' && current <= '9' || current >= 'a' && current <= 'f' || current >= 'A' && current <= 'F'
	}

	private fun isEndOfText(): Boolean {
		return index >= string.length
	}
}