Handlebars.registerHelper('addstyledeclaration', function(str) {
    var html = [];
    if (phpjs.is_string(str)) {
        html.push('<style type="text/css">'+str+'</style>');
    }
    return new Handlebars.SafeString(html.join(''));
});