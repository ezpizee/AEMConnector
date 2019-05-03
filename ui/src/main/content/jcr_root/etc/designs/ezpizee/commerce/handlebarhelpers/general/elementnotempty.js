Handlebars.registerHelper('elementnotempty', function(list, key) {
    var options = arguments[arguments.length-1];
    if ((phpjs.is_array(list) || phpjs.is_object(list)) && list[key]) {
        return options.fn(this);
    }
    return options.inverse(this);
});