Handlebars.registerHelper('phpjs', function() {
    var fn = arguments[0]||'';
    if (fn && arguments[1] && phpjs[fn]) {
        var args = [];
        for (var i = 1; i < arguments.length-1; i++) {args.push(arguments[i]);}
        if (args.length) {
            if (args.length === 1) {return phpjs[fn](args[0]);}
            else {return phpjs[fn].apply(this, args);}
        }
    }
    return '';
});