Handlebars.registerHelper('raw', function() {
    var options = arguments[arguments.length-1];
    return options.fn(this);
});