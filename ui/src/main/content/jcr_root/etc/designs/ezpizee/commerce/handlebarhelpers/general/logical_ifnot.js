Handlebars.registerHelper('ifnot', function(conditional, options) {
    if (conditional && conditional !== 'false' && conditional !== '0') {
        return options.inverse(this);
    }
    return options.fn(this);
});