Handlebars.registerHelper('jsscript', function() {
    var fn, arg, num = arguments.length;
    if (num > 1) {
        fn = arguments[0];
        if (num > 2) {
            arg = [];
            for(var i = 1; i < num - 1; i++) {
                arg.push(arguments[i]);
            }
        }
    }

    var output = "";
    if(fn) {
        try {if (WC.utilities.isBase64Encoded(fn)) {fn = phpjs.base64_decode(fn);}} catch (err) {}
        if (arg) {
            if (phpjs.is_object(arg) || phpjs.is_array(arg)) {
                output = '<script>'+fn+'('+phpjs.json_encode(arg)+')</script>';
            }
            else {
                output = '<script>'+fn+'("'+arg+'")</script>';
            }
        }
        else {
            output = '<script>'+fn+'</script>';
        }
    }

    return output;
});