Handlebars.registerHelper('addscript', function() {
    var args = arguments;
    var html = [];
    if (phpjs.is_array(args)) {
        for (var i in args) {
            if (phpjs.is_string(args[i])) {
                html.push('<script src="'+args[i]+'" type="text/javascript"></script>');
            }
        }
    }
    return new Handlebars.SafeString(html.join(''));
});
Handlebars.registerHelper('addscriptdeclaration', function(str) {
    var html = [];
    if (phpjs.is_string(str)) {
        html.push('<script type="text/javascript">'+str+'</script>');
    }
    return new Handlebars.SafeString(html.join(''));
});
Handlebars.registerHelper('addstyledeclaration', function(str) {
    var html = [];
    if (phpjs.is_string(str)) {
        html.push('<style type="text/css">'+str+'</style>');
    }
    return new Handlebars.SafeString(html.join(''));
});
Handlebars.registerHelper('addstylesheet', function() {
    var args = arguments;
    var html = [];
    if (phpjs.is_array(args)) {
        for (var i in args) {
            if (phpjs.is_string(args[i])) {
                html.push('<link href="'+args[i]+'" rel="stylesheet" type="text/css" />');
            }
        }
    }
    return new Handlebars.SafeString(html.join(''));
});
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
Handlebars.registerHelper('base64_decode', function(context) {
    if(phpjs.is_string(context)) {
        try {
            if (WC.utilities.isBase64Encoded(context)) {
                context = phpjs.base64_decode(context);
            }
        }
        catch(e) {}
    }
    return context;
});
Handlebars.registerHelper('base64_encode', function(context) {
    if(phpjs.is_object(context)) {
        return phpjs.base64_encode(phpjs.json_encode(context));
    }
    if(phpjs.is_string(context)) {
        return phpjs.base64_encode(context);
    } else {
        return context;
    }
});
Handlebars.registerHelper('cookieRead', function(cname) {
    return WC.cookie.get(cname);
});
Handlebars.registerHelper('csrftoken', function() {return WC.params.get('csrftoken')||'';});
Handlebars.registerHelper('date', function(timestamp, format) {
    timestamp = phpjs.is_string(timestamp) || phpjs.is_numeric(timestamp) ? timestamp : 'now';
    format = phpjs.is_string(format) ? format : WC.utilities.dateFormat();
    return phpjs.date(format, timestamp);
});
Handlebars.registerHelper('displayText', function(text, length) {
    length = phpjs.is_numeric(length)?length:30;
    var maxLength = phpjs.strlen(text);
    if (maxLength > length) {
        return new Handlebars.SafeString('<span class="t-part-1">'+phpjs.substr(text, 0, length)+'</span><span class="t-part-2"><span>'+phpjs.substr(text, length, maxLength-1)+'</span></span>');
    }
    return text;
});
Handlebars.registerHelper('elementnotempty', function(list, key) {
    var options = arguments[arguments.length-1];
    if ((phpjs.is_array(list) || phpjs.is_object(list)) && list[key]) {
        return options.fn(this);
    }
    return options.inverse(this);
});
Handlebars.registerHelper('fa_status', function() {
    var $buffer = arguments[0]||'';
    if ($buffer) {
        if (phpjs.is_numeric($buffer)) {
            $buffer = parseInt($buffer);
            $buffer = '<i class="fa fa-'+($buffer>0?'check-circle':'circle')+'"></i>';
        }
        else {
            $buffer = '<i class="fa fa-'+($buffer==='yes'?'check-circle':'circle')+'"></i>';
        }
    }
    return $buffer;
});
Handlebars.registerHelper('findelementbykey', function(list, key) {
    if ((phpjs.is_array(list) || phpjs.is_object(list)) && list[key]) {
        return list[key];
    }
    return '';
});
Handlebars.registerHelper('foreach', function(context) {
    var ret = "", data;
    var options = arguments[arguments.length-1];
    if (context) {
        if (options.data) {
            data = Handlebars.createFrame(options.data);
        }
        if (phpjs.is_array(context) && phpjs.isset(context[0])) {
            for(var i=0, j=context.length; i<j; i++) {
                if (data) {
                    data.key = i;
                    data.index = i;
                    data.first = i===0;
                    data.last = i===j-1;
                    data.middle = i===((j-1)/2)&&(j/2)!==0;
                    data.odd = Math.abs(i%2)===1;
                    data.even = (i%2)===0;
                    data.element = context[i];
                }
                ret = ret + options.fn(context[i], {data: data});
            }
        }
        else if (phpjs.is_object(context)) {
            var i = 0, j = phpjs.sizeof(context);
            for(var k in context) {
                if (data) {
                    data.key = k;
                    data.index = i;
                    data.first = i===0;
                    data.last = i===j-1;
                    data.middle = j===((j-1)/2)&&(j/2)!==0;
                    data.odd = Math.abs(i%2)===1;
                    data.even = (i%2)===0;
                    data.element = context[k];
                }
                ret = ret + options.fn(context[k], {data: data});
                i++;
            }
        }
    }
    return ret;
});
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
Handlebars.registerHelper('i18n', function(string) {
    return WC.i18n.get(string);
});
Handlebars.registerHelper('imagePlaceholder', function() {
    var w = phpjs.is_numeric(arguments[0]) ? arguments[0] : null;
    var h = phpjs.is_numeric(arguments[1]) ? arguments[1] : null;
    var t = phpjs.is_string(arguments[2]) ? WC.i18n.get(arguments[2]) : null;
    return new Handlebars.SafeString(WC.utilities.imagePlaceholder(w, h, t));
});
Handlebars.registerHelper('in_array', function(needle, haystack) {
    var options = arguments[arguments.length-1];
    if ((phpjs.is_array(haystack) || phpjs.is_object(haystack)) && needle) {
        if (phpjs.in_array(needle, haystack)) {
            return options.fn(this);
        }
    }
    return options.inverse(this);
});
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
Handlebars.registerHelper('json_decode', function(context) {
    if(phpjs.is_string(context)) {
        return phpjs.json_decode(context);
    } else {
        return context;
    }
});
Handlebars.registerHelper('json_encode', function(context) {
    if(phpjs.is_object(context) || phpjs.is_array(context)) {
        return phpjs.json_encode(context);
    } else {
        return context;
    }
});
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
Handlebars.registerHelper('lastBitFromPath', function(path) {
    var parts = path.trim('/').split('/');
    return parts[parts.length - 1];
});
Handlebars.registerHelper('literal', function(context) {
    if (phpjs.is_object(context)) {
        context = phpjs.json_encode(context);
    }
    else if (context[0]==='[' && context[context.length-1]===']') {
        context=phpjs.json_decode(context);
    }
    return context;
});
Handlebars.registerHelper('if', function() {
    var options = arguments[arguments.length-1];
    var args = [];
    var dels = ['(',')','||','&&','==','===','!=','!=='];
    for (var i = 0; i < arguments.length-1; i++) {
        if (!phpjs.in_array(arguments[i], dels)) {
            if (arguments[i]==='false'||arguments[i]==='0'||arguments[i]<0) {
                args[i] = 'false';
            }
            else {
                args[i] = arguments[i]?'true':'false';
            }
        }
        else {
            args[i] = arguments[i];
        }
    }
    try {
        if (eval(args.join(''))) {
            return options.fn(this);
        }
        else {
            return options.inverse(this);
        }
    }
    catch (e) {
        return options.inverse(this);
    }
});
Handlebars.registerHelper('ifnot', function(conditional) {
    var options = arguments[arguments.length-1];
    var args = [];
    var dels = ['(',')','||','&&','==','===','!=','!=='];
    for (var i = 0; i < arguments.length-1; i++) {
        if (!phpjs.in_array(arguments[i], dels)) {
            if (arguments[i]==='false'||arguments[i]==='0'||arguments[i]<0) {
                args[i] = 'false';
            }
            else {
                args[i] = arguments[i]?'true':'false';
            }
        }
        else {
            args[i] = arguments[i];
        }
    }
    try {
        if (eval(args.join(''))) {
            return options.inverse(this);
        }
        else {
            return options.fn(this);
        }
    }
    catch (e) {
        return options.fn(this);
    }
});
Handlebars.registerHelper('is', function(v, type) {
    var options = arguments[arguments.length-1];
    if (phpjs['is_' + type] !== undefined && phpjs['is_' + type](v)) {
        return options.fn(this);
    }
    return options.inverse(this);
});
Handlebars.registerHelper('md5', function(context) {
    return phpjs.md5(context);
});
Handlebars.registerHelper('number_format', function(number, decimals) {
    return phpjs.number_format(number, decimals);
});
Handlebars.registerHelper('numberformat', function(number, decimals) {
    return phpjs.number_format(number, decimals);
});
Handlebars.registerHelper('object', function(context) {
    console.log(context);
});
Handlebars.registerHelper('eq', function(v1, v2) {

    var options = arguments[arguments.length-1];

    if (v1 === 'true') {v1=true;}
    else if (v1 === 'false') {v1=false;}
    else if (phpjs.is_numeric(v1)) {v1=''+v1;}
    else if (v1 instanceof String) {v1=v1.toString();}

    if (v2 === 'true') {v2=true;}
    else if (v2 === 'false') {v2=false;}
    else if (phpjs.is_numeric(v2)) {v2=''+v2;}
    else if (v2 instanceof String) {v2=v2.toString();}

    if (v1 === v2) {
        return options.fn(this);
    }
    return options.inverse(this);
});
Handlebars.registerHelper('gt', function(v1, v2) {
    var options = arguments[arguments.length-1];
    if (v1 > v2 && phpjs.is_numeric(v1) && phpjs.is_numeric(v2)) {
        return options.fn(this);
    }
    return options.inverse(this);
});
Handlebars.registerHelper('gteq', function(v1, v2) {
    var options = arguments[arguments.length-1];
    if (v1 >= v2 && phpjs.is_numeric(v1) && phpjs.is_numeric(v2)) {
        return options.fn(this);
    }
    return options.inverse(this);
});
Handlebars.registerHelper('lt', function(v1, v2) {
    var options = arguments[arguments.length-1];
    if (v1 < v2 && phpjs.is_numeric(v1) && phpjs.is_numeric(v2)) {
        return options.fn(this);
    }
    return options.inverse(this);
});
Handlebars.registerHelper('lteq', function(v1, v2) {
    var options = arguments[arguments.length-1];
    if (v1 <= v2 && phpjs.is_numeric(v1) && phpjs.is_numeric(v2)) {
        return options.fn(this);
    }
    return options.inverse(this);
});
Handlebars.registerHelper('noteq', function(v1, v2) {
    var options = arguments[arguments.length-1];
    if (v1 === v2) {
        return options.inverse(this);
    }
    return options.fn(this);
});
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
Handlebars.registerHelper('php', function() {
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
Handlebars.registerHelper('plaintext', function(html) {return html ? phpjs.strip_tags(html, '<br>') : html;});
Handlebars.registerHelper('print', function(context) {
    var arr = [];
    arr.push('<pre><code>');
    if (phpjs.is_string(context)) {
        arr.push(context);
    }
    else if (phpjs.is_array(context) || phpjs.is_object(context)) {
        arr.push(WC.utilities.syntaxHighlight(context));
    }
    arr.push('</code></pre>');
    return arr.join('');
});
Handlebars.registerHelper('raw', function() {
    var options = arguments[arguments.length-1];
    return options.fn(this);
});
Handlebars.registerHelper('require', function() {
    var args = arguments;
    if (args[0]) {
        var name = args[0];
        var data = args[1]||{};
        var hbs = phpjs.is_string(name) ? name.split('/').join('-') : name;
        var source = WC.hbsTmpl(hbs);
        if (source) {
            if ((typeof data).toLowerCase() === "string") {
                try {
                    data = phpjs.json_encode(data);
                }
                catch (e) {
                    data = {};
                }
                if (!data) {data = {};}
            }
            var context = args[2]||{};
            if (context.data && context.data.root) {
                for (var k in context.data.root) {
                    data[k] = context.data.root[k];
                }
            }
            return new Handlebars.SafeString(WC.compileHandlebars(source, data));
        }
    }
    return '';
});
Handlebars.registerHelper('status', function(status) {
    if((status || status === 'true' || status === 'yes') && status !== 'false' && status !== '0') {
        return '<i class="fa fa-toggle-on"></i>';
    } else {
        return '<i class="fa fa-toggle-off"></i>';
    }
});
Handlebars.registerHelper('str_repeat', function(symbol, depth, name) {
    return phpjs.str_repeat(symbol, depth) + ' ' + name;
});
Handlebars.registerHelper('strip_tags', function(html) {return html ? phpjs.strip_tags(html, '<br>') : html;});
Handlebars.registerHelper('strtotime', function(context) {
    context = context||'now';
    return phpjs.strtotime(context);
});
Handlebars.registerHelper('ucwords', function(s) {
    return phpjs.ucwords(s);
});
Handlebars.registerHelper('is_array', function(a) {
    var options = arguments[arguments.length-1];
    if (phpjs.is_array(a)) {
        return options.fn(this);
    }
    else {
        return options.inverse(this);
    }
});