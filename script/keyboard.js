
$(document).ready(function() {
    $('body').keydown(function(event) {
        Kotlin.modules.timeline.timeline.setPressedKeysFromJavascript(null, event.keyCode);
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
        var ch = String.fromCharCode(event.keyCode);
        Kotlin.modules.timeline.timeline.setPressedKeysFromJavascript(ch, event.keyCode);
        return false
    });


});