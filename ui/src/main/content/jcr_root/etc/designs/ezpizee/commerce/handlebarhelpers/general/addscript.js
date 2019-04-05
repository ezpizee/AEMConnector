Handlebars.registerHelper('addscript', function() {
    var args = arguments;
    var html = [];
    if (phpjs.is_array(args)) {
        for (var i in args) {
            if (phpjs.is_string(args[i])) {
                html.push('<script src="'+args[i]+'" type="text/javascript"></script>');
            }
        }
    }
    return new Handlebars.SafeString(html.join(''));
});