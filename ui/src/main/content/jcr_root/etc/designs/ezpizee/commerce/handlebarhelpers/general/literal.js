Handlebars.registerHelper('literal', function(context) {
    if (phpjs.is_object(context)) {
        context = phpjs.json_encode(context);
    }
    else if (context[0]==='[' && context[context.length-1]===']') {
        context=phpjs.json_decode(context);
    }
    return context;
});