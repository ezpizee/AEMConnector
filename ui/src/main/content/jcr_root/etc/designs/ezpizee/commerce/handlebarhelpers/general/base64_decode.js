Handlebars.registerHelper('base64_decode', function(context) {
    if(phpjs.is_string(context)) {
        try {
            if (WC.utilities.isBase64Encoded(context)) {
                context = phpjs.base64_decode(context);
            }
        }
        catch(e) {}
    }
    return context;
});