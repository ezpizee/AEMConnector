Handlebars.registerHelper('in_array', function(needle, haystack) {
    var options = arguments[arguments.length-1];
    if ((phpjs.is_array(haystack) || phpjs.is_object(haystack)) && needle) {
        if (phpjs.in_array(needle, haystack)) {
            return options.fn(this);
        }
    }
    return options.inverse(this);
});