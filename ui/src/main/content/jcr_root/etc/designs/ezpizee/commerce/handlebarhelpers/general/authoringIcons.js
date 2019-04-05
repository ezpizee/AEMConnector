Handlebars.registerHelper('authoringIcons', function(icon) {
    if (icon === 'add') {
        return '<i class="fa fa-plus"></i>';
    }
    else if (icon === 'edit') {
        return '<i class="fa fa-pen"></i>';
    }
    else if (icon === 'install') {
        return '<i class="fa fa-upload"></i>';
    }
    else if (icon === 'uninstall') {
        return '<i class="fa fa-unlink"></i>';
    }
    else if (icon === 'delete' || icon === 'remove') {
        return '<i class="fa fa-trash"></i>';
    }
});