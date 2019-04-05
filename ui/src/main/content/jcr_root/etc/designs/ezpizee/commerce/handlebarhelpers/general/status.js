Handlebars.registerHelper('status', function(status) {
    if((status || status === 'true' || status === 'yes') && status !== 'false' && status !== '0') {
        return '<i class="fa fa-toggle-on"></i>';
    } else {
        return '<i class="fa fa-toggle-off"></i>';
    }
});