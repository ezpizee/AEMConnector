function rteStandardConfig(selector) {
    var e = $(selector);
    if (e.length) {
        var id = e.attr('id');
        if (!id) {
            id = phpjs.uniqid('rte-');
            e.attr('id', id);
        }
        var editor = CKEDITOR.inline(id);
    }
}