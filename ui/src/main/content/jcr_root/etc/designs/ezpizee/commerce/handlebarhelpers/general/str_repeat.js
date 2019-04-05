Handlebars.registerHelper('str_repeat', function(symbol, depth, name) {
    return phpjs.str_repeat(symbol, depth) + ' ' + name;
});