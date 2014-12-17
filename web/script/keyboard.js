
$(document).ready(function() {
    $('body').keydown(function(event) {
        console.log("keydown: " + event.keyCode);
        Kotlin.modules.timeline.timeline.keyDown(null, event.keyCode);
        var keycode = event.keyCode;

        var valid =
            (keycode > 47 && keycode < 58)   || // number keys
            keycode == 32 || keycode == 13   || // spacebar & return key(s) (if you want to allow carriage returns)
            (keycode > 64 && keycode < 91)   || // letter keys
            (keycode > 95 && keycode < 112)  || // numpad keys
            (keycode > 185 && keycode < 193) || // ;=,-./` (in order)
            (keycode > 218 && keycode < 223);   // [\]' (in order)
        return valid;
    });
    $('body').keypress(function(event) {
        console.log("keypress: " + event.keyCode);
        var ch = String.fromCharCode(event.keyCode);
        Kotlin.modules.timeline.timeline.keyDown(ch, event.keyCode);
        return false
    });
    $('canvas').mouseup(function(event) {
        event.stopPropagation();
        event.preventDefault();
        Kotlin.modules.timeline.timeline.onMouseUp(event.which);
    });
    $('canvas').mousedown(function(event) {
        event.stopPropagation();
        event.preventDefault();
        Kotlin.modules.timeline.timeline.onMouseDown(event.which);
    });
    // using the event helper
    $('canvas').mousewheel(function(event) {
        event.stopPropagation();
        event.preventDefault();
        Kotlin.modules.timeline.timeline.onMouseScroll(event.deltaY);
    });
    $('canvas').on("contextmenu",function(e){
        e.stopPropagation();
        e.preventDefault();
    });
});
function jsSetCursor(cursorStyle) {
    $("canvas").css('cursor', cursorStyle)
}
