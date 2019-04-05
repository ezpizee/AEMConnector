Handlebars.registerHelper('date', function(timestamp, format) {
    timestamp = phpjs.is_string(timestamp) || phpjs.is_numeric(timestamp) ? timestamp : 'now';
    format = phpjs.is_string(format) ? format : WC.utilities.dateFormat();
    return phpjs.date(format, timestamp);
});