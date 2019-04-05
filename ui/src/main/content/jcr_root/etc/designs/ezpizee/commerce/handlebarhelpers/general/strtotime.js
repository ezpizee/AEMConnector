Handlebars.registerHelper('strtotime', function(context) {
    context = context||'now';
    return phpjs.strtotime(context);
});