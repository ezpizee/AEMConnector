Handlebars.registerHelper('jsfile', function(path) {
    
    var files = [];
    
    if (phpjs.is_string(path)) {
        files = path.split(',');
    }
    else if (phpjs.is_array(path)) {
        files = path;
    }
    else if (phpjs.is_object(path)) {
        for (var i in path) {
            files.push(path[i]);
        }
    }
    
    for (var i = 0; i < files.length; i++) {
        WC.utilities.loadJS(files[i]);
    }
    
    return '';
});