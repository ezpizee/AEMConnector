Handlebars.registerHelper('eq', function(v1, v2) {
    var options = arguments[arguments.length-1];
    if (''+v1 === ''+v2) {
        return options.fn(this);
    }
    return options.inverse(this);
});