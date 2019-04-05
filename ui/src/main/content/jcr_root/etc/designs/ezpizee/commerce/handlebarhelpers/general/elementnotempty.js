Handlebars.registerHelper('elementnotempty', function(list, key, options) {
    if ((phpjs.is_array(list) || phpjs.is_object(list)) && list[key]) {
        return options.fn(this);
    }
    return options.inverse(this);
});