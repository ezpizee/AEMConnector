Handlebars.registerHelper('noteq', function(v1, v2) {
    var options = arguments[arguments.length-1];
    if (v1 === v2) {
        return options.inverse(this);
    }
    return options.fn(this);
});