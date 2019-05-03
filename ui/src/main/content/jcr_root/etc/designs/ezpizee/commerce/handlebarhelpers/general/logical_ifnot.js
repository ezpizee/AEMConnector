Handlebars.registerHelper('ifnot', function(conditional) {
    var options = arguments[arguments.length-1];
    if (conditional && conditional !== 'false' && conditional !== '0') {
        return options.inverse(this);
    }
    return options.fn(this);
});