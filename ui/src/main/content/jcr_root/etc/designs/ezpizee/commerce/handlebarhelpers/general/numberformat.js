Handlebars.registerHelper('numberformat', function(number, decimals) {
    return phpjs.number_format(number, decimals);
});