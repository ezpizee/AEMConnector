Handlebars.registerHelper('displayText', function(text, length) {
    length = phpjs.is_numeric(length)?length:30;
    var maxLength = phpjs.strlen(text);
    if (maxLength > length) {
        return new Handlebars.SafeString('<span class="t-part-1">'+phpjs.substr(text, 0, length)+'</span><span class="t-part-2"><span>'+phpjs.substr(text, length, maxLength-1)+'</span></span>');
    }
    return text;
});