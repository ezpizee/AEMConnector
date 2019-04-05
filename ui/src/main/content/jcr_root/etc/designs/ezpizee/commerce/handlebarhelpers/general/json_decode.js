Handlebars.registerHelper('json_decode', function(context) {
    if(phpjs.is_string(context)) {
        return phpjs.json_decode(context);
    } else {
        return context;
    }
});