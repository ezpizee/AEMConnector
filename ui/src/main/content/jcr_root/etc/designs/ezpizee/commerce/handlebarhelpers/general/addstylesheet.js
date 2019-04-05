Handlebars.registerHelper('addstylesheet', function() {
    var args = arguments;
    var html = [];
    if (phpjs.is_array(args)) {
        for (var i in args) {
            if (phpjs.is_string(args[i])) {
                html.push('<link href="'+args[i]+'" rel="stylesheet" type="text/css" />');
            }
        }
    }
    return new Handlebars.SafeString(html.join(''));
});