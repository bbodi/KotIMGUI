

function localStorage_setItem(key, value) {
    var type = typeof value;

    if ((type !== 'string' && type !== 'number' && type !== 'boolean') || type === null) {
        value = JSON.stringify(value);
    }

    localStorage.setItem(key, value);

    return arguments[1];
}

function localStorage_getItem(key) {
    var value = localStorage.getItem(key),
        valueLength,
        isNeedToParse;

    if (value !== null) {
        valueLength = value.length;
        isNeedToParse =
            (value.substring(0, 1) === '[' && value.substring(valueLength - 1, valueLength) === ']') ||
            (value.substring(0, 1) === '{' && value.substring(valueLength - 1, valueLength) === '}');

        /*if (isNeedToParse) {
            value = JSON.parse(value);
        }*/
    }

    return value;
};