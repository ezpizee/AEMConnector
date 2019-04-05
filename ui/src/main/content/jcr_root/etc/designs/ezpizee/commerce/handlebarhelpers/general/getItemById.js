Handlebars.registerHelper('getItemById', function(items, id) {
    var item = null;
    if (phpjs.is_array(items)) {
        for (var i = 0; i < items.length; i++) {
            if (items[i]['id'] === id) {
                item = items[i];
                break;
            }
        }
    }
    else if (phpjs.is_object(items)) {
        if (typeof items[id] !== "undefined") {
            item = items[id];
        }
        else {
            for (var key in items) {
                if (items[key]['id'] === id) {
                    item = items[key];
                    break;
                }
            }
        }
    }

    return item;
});