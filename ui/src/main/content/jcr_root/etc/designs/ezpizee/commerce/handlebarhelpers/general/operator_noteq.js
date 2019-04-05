Handlebars.registerHelper('noteq', function(v1, v2, options) {
    if (v1 === v2) {
        return options.inverse(this);
    }
    return options.fn(this);
});