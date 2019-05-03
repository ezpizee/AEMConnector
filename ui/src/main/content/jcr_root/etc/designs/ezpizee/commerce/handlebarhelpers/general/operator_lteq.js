Handlebars.registerHelper('lteq', function(v1, v2) {
    var options = arguments[arguments.length-1];
    if (v1 <= v2 && phpjs.is_numeric(v1) && phpjs.is_numeric(v2)) {
        return options.fn(this);
    }
    return options.inverse(this);
});