Handlebars.registerHelper('is', function(v, type) {
    var options = arguments[arguments.length-1];
    if (phpjs['is_' + type] !== undefined && phpjs['is_' + type](v)) {
        return options.fn(this);
    }
    return options.inverse(this);
});