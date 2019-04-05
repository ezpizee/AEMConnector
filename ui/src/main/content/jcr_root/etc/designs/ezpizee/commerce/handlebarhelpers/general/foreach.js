Handlebars.registerHelper('foreach', function(context, options) {
    var ret = "", data;
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