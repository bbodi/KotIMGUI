package timeline

// full list: http://www.w3schools.com/cssref/playit.asp?filename=playcss_cursor&preval=copy
public enum class CursorStyle(val htmlName: String) {
	Auto: CursorStyle("auto")
	CrossHair: CursorStyle("crosshair")
	Default: CursorStyle("default")
	EastResize: CursorStyle("e-resize")
	Grab: CursorStyle("grab")
	Help: CursorStyle("help")
	Move: CursorStyle("move")
	NorthResize: CursorStyle("n-resize")
	NorthEastResize: CursorStyle("ne-resize")
	NorthWestResize: CursorStyle("nw-resize")
	Pointer: CursorStyle("pointer")
	Progress: CursorStyle("progress")
	SouthResize: CursorStyle("s-resize")
	SouthEastResize: CursorStyle("se-resize")
	SouthWestResize: CursorStyle("sw-resize")
	Text: CursorStyle("text")
	WestResize: CursorStyle("w-resize")
	Wait: CursorStyle("wait")
	NotAllowed: CursorStyle("not-allowed")
	NoDrop: CursorStyle("no-drop")
	Initial: CursorStyle("initial")
}

fun setCursor(cursorStyle: CursorStyle) {
	jsSetCursor(cursorStyle.htmlName)
}

native
private fun jsSetCursor(cursorStyle: String) {

}