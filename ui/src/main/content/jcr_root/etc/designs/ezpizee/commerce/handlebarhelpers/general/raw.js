Handlebars.registerHelper('raw', function(options) {
    return options.fn(this);
});