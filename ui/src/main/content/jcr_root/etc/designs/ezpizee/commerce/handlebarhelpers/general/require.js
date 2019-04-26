Handlebars.registerHelper('require', function() {
    var args = arguments;
    if (args[0]) {
        var name = args[0];
        var data = args[1]||{};
        var hbs = phpjs.is_string(name) ? name.split('/').join('-') : name;
        var source = WC.hbsTmpl(hbs);
        if (source) {
            var context = args[2]||{};
            if (context.data && context.data.root) {
                for (var k in context.data.root) {
                    data[k] = context.data.root[k];
                }
            }
            return new Handlebars.SafeString(WC.compileHandlebars(source, data));
        }
    }
    return '';
});