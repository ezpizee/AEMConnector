Handlebars.registerHelper('cookieRead', function(cname) {
    return WC.cookie.get(cname);
});