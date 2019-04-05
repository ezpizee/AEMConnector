Handlebars.registerHelper('imagePlaceholder', function() {
    var w = phpjs.is_numeric(arguments[0]) ? arguments[0] : null;
    var h = phpjs.is_numeric(arguments[1]) ? arguments[1] : null;
    var t = phpjs.is_string(arguments[2]) ? WC.i18n.get(arguments[2]) : null;
    return new Handlebars.SafeString(WC.utilities.imagePlaceholder(w, h, t));
});