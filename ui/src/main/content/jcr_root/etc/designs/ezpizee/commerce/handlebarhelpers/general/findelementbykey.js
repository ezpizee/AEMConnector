Handlebars.registerHelper('findelementbykey', function(list, key) {
    if ((phpjs.is_array(list) || phpjs.is_object(list)) && list[key]) {
        return list[key];
    }
    return '';
});