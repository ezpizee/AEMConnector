Handlebars.registerHelper('base64_encode', function(context) {
    if(phpjs.is_object(context)) {
        return phpjs.base64_encode(phpjs.json_encode(context));
    }
    if(phpjs.is_string(context)) {
        return phpjs.base64_encode(context);
    } else {
        return context;
    }
});