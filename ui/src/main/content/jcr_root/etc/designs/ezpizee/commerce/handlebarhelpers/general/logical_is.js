Handlebars.registerHelper('is', function(v, type, options) {
    if (phpjs['is_' + type] !== undefined && phpjs['is_' + type](v)) {
        return options.fn(this);
    }
    return options.inverse(this);
});