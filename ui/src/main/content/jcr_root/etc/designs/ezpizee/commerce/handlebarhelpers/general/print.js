Handlebars.registerHelper('print', function(context) {
    var arr = [];
    arr.push('<pre><code>');
    if (phpjs.is_string(context)) {
        arr.push(context);
    }
    else if (phpjs.is_array(context) || phpjs.is_object(context)) {
        arr.push(WC.utilities.syntaxHighlight(context));
    }
    arr.push('</code></pre>');
    return arr.join('');
});