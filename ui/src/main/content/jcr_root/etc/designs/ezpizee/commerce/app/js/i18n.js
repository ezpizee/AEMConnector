WC.i18n = function() {
    var that = {};
    var data = null;
    that.get = function(key, defaultValue) {
        if (data === null) {
            data = WC.params.get('language', {});
        }
        var s = key ? data[key]||data[key.toUpperCase()]||defaultValue||key : defaultValue;
        if (s && phpjs.is_string(s)) {
            s = phpjs.str_replace(['&lt;','&gt;'], ['<','>'], s);
        }
        return s;
    };
    that.set = function(key, value) {data[key] = value;};
    that.merge = function(obj) {if (phpjs.is_object(obj)) {for (var k in obj) {if (!data[k]) {data[k] = obj[k];}}}};
    return that;
}();