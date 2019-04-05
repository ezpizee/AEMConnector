Handlebars.registerHelper('lastBitFromPath', function(path) {
    var parts = path.trim('/').split('/');
    return parts[parts.length - 1];
});