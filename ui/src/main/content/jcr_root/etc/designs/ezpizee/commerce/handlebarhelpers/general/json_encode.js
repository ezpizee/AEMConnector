Handlebars.registerHelper('json_encode', function(context) {
    if(phpjs.is_object(context)) {
        return phpjs.json_encode(context);
    } else {
        return context;
    }
});