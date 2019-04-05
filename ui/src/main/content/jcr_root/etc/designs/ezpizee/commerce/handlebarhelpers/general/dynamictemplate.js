Handlebars.registerHelper('dynamictemplate', function(root, name, context, options) {
    var f = Handlebars.partials[phpjs.str_replace('//', '/', root + '/' + name)];
    if (!f) {
        return 'Partial not loaded';
    }
    return new Handlebars.SafeString(f(context));
});