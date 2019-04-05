Handlebars.registerHelper('addscriptdeclaration', function(str) {
    var html = [];
    if (phpjs.is_string(str)) {
        html.push('<script type="text/javascript">'+str+'</script>');
    }
    return new Handlebars.SafeString(html.join(''));
});