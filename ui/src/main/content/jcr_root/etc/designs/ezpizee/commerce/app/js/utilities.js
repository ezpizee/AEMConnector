WC.utilities = function ($) {
    var that = {};

    that.parseUri = function(url) {
        var output = {protocol:'',scheme:'',path:'',port:'',host:'',query_string:'',params:{},pfx:''};
        if (phpjs.is_string(url)) {
            var parts = url.split('?');
            var parts1 = parts[0].split('/');
            var parts2 = parts.length > 1 ? parts[1] : null;
            if (url.startsWith('http://') || url.startsWith('https://')) {
                output.protocol = parts1[0].replace(':', '');
                output.scheme = parts1[0] + '//';
                var parts3 = parts[2].split(':');
                output.host = parts2[0];
                if (parts3.length > 1) {
                    output.port = parts3[0];
                    output.pfx = output.scheme+output.host+':'+output.port;
                }
                else {
                    output.pfx = output.scheme+output.host;
                }
                output.path = parts[0].replace(output.pfx);
            }
            if (parts2 !== null) {
                output.query_string = parts[1];
                var params = output.query_string.split('&');
                for(var i in params) {
                    var arr = params[i].split('=');
                    output.params[arr[0]] = arr[1]||'';
                }
            }
        }
    };

    that.camelCaseName = function (property) {
        var parts = property.split('-');
        var newProperty = [];
        newProperty.push(parts[0]);
        for (var i = 1; i < parts.length; i++) {
            newProperty.push(parts[i].charAt(0).toUpperCase() + parts[i].slice(1));
        }
        return newProperty.join('');
    };
    that.hyphenatedName = function (property) {
        property = property.replace(/[\W_]+/g, '-');
        var newProperty = '';
        for (var i = 0; i < property.length; i++) {
            if (that.isUpperCase(property.charAt(i))) {
                newProperty = newProperty + '-' + property.charAt(i).toLowerCase();
            }
            else {
                newProperty = newProperty + property.charAt(i);
            }
        }
        return newProperty;
    };
    that.underscoreName = function (property) {
        property = property.replace(/[\W_]+/g, '_');
        var newProperty = '';
        for (var i = 0; i < property.length; i++) {
            if (that.isUpperCase(property.charAt(i))) {
                newProperty = newProperty + '_' + property.charAt(i).toLowerCase();
            }
            else {
                newProperty = newProperty + property.charAt(i);
            }
        }
        return newProperty;
    };
    that.isUpperCase = function (s) {
        return s === s.toUpperCase();
    };
    that.isLowerCase = function (s) {
        return s === s.toLowerCase();
    };
    that.extractScriptsAsArray = function (html) {
        var parts = html.split('<\/script>');
        var scripts = [];
        for (var i in parts) {
            parts[i] = parts[i].split('<script>');
            if (parts[i].length === 2) {
                scripts.push(parts[i][parts[i].length - 1]);
            }
            else {
                parts[i] = parts[i][0].split('<script type="text/javascript">');
                if (parts[i].length === 2) {
                    scripts.push(parts[i][parts[i].length - 1]);
                }
            }
        }
        return scripts;
    };
    that.executeScripts = function (scripts) {
        if (((typeof scripts).toLowerCase() === 'object' || (typeof scripts).toLowerCase() === 'array') && scripts.length) {
            for (var i in scripts) {
                if (scripts[i]) {
                    eval(scripts[i]);
                }
            }
        }
    };
    that.invokeScriptBlock = function (html) {
        if ((typeof html).toLowerCase() === 'string') {
            that.executeScripts(that.extractScriptsAsArray(html));
        }
    };

    var dynamicCSS = [], dynamicJS = [];
    that.scriptDeclaration = function (str, e) {
        if (str) {
            var hash = phpjs.md5(str);
            if (!phpjs.in_array(hash, dynamicJS)) {
                var dom = document.createElement('script');
                dom.innerHTML = str;
                if (e) {
                    e.appendChild(dom);
                }
                else {
                    document.getElementsByTagName("head")[0].appendChild(dom);
                }
                dynamicJS.push(hash);
            }
        }
    };
    that.styleSheetDeclaration = function (str, e) {
        if (str) {
            var hash = phpjs.md5(str);
            if (!phpjs.in_array(hash, dynamicCSS)) {
                var dom = document.createElement('style');
                dom.innerHTML = str;
                if (e) {
                    e.appendChild(dom);
                }
                else {
                    document.getElementsByTagName("head")[0].appendChild(dom);
                }
                dynamicCSS.push(hash);
            }
        }
    };

    that.loadCSS = function (files, attrs, e) {
        if (files) {
            if (phpjs.is_array(files) || phpjs.is_object(files)) {
                for (var i in files) {
                    _loadCSS(files[i], attrs, e);
                }
            } else {
                _loadCSS(files, attrs, e);
            }
        }
    };
    that.loadJS = function (files, attrs, e) {
        if (files) {
            if (phpjs.is_array(files) || phpjs.is_object(files)) {
                for (var i in files) {
                    _loadJS(files[i], attrs, e);
                }
            } else {
                _loadJS(files, attrs, e);
            }
        }
    };

    that.isJSON = function (str) {
        try {
            JSON.parse(str);
        } catch (e) {
            return false;
        }
        return true;
    };
    that.uriFromUrlHash = function () {
        return window.location.hash ? window.location.hash.replace('#', '') : '';
    };

    function _loadCSS(url, attrs, e) {
        if (url) {
            if (phpjs.in_array(url, dynamicCSS)) {
                var dom = $('link[href="' + url + '"]');
                if (dom.length) {
                    dom.remove();
                }
            }
            var link = document.createElement("link");
            link.rel = "stylesheet";
            link.href = url;
            link.type = "text/css";
            if (attrs) {
                for (var k in attrs) {
                    link[k] = attrs[k];
                }
            }
            if (e) {
                e.appendChild(link);
            }
            else {
                document.getElementsByTagName("head")[0].appendChild(link);
            }
            dynamicCSS.push(url);
        }
    }

    function _loadJS(url, attrs, e) {
        if (url) {
            if (phpjs.in_array(url, dynamicJS)) {
                var dom = $('script[src="' + url + '"]');
                if (dom.length) {
                    dom.remove();
                }
            }
            var script = document.createElement("script");
            script.src = url;
            script.async = true;
            if (attrs) {
                for (var k in attrs) {
                    script[k] = attrs[k];
                }
            }
            if (e) {
                e.appendChild(script);
            }
            else if (document.body) {
                document.body.appendChild(script);
            }
            else {
                document.getElementsByTagName("head")[0].appendChild(script);
            }
            dynamicJS.push(url);
        }
    }

    that.scrollbar = function () {
        var elements = $('[data-scrollbar]');
        if (elements.length) {
            elements.each(function () {
                var element = $(this);
                if (!element.attr(WC.constants.ATTR_DATA_HOOK_PFX + '-scrollbar')) {
                    var settings = element.attr('data-scrollbar');
                    if (that.isJSONString(settings)) {
                        settings = phpjs.json_decode(settings);
                    } else {
                        settings = {
                            theme: "minimal-dark",
                            scrollInertia: 0,
                            advanced: {updateOnContentResize: true},
                            scrollButtons: {"enable": true},
                            scrollbarPosition: "inside"
                        };
                    }
                    element.mCustomScrollbar(settings);
                    element.attr(WC.constants.ATTR_DATA_HOOK_PFX + '-scrollbar', true);
                }
            });
        }
    };
    that.getScrollbarContainer = function (e) {
        if (e.find('>.mCustomScrollBox').length) {
            e = e.find('>.mCustomScrollBox');
            var id = e.attr('id');
            if (e.find('#' + id + '_container').length) {
                e = $('#' + id + '_container');
            }
        }
        return e;
    };
    that.populateContentToScrollbarContainer = function (e, html) {
        e = that.getScrollbarContainer(e);
        e.html(html);
    };
    that.destroyScrollbar = function (e) {
        if (phpjs.is_string(e)) {
            e = $(e);
        }
        e.mCustomScrollbar('destroy');
    };

    that.isBase64Encoded = function (str) {
        try {
            return btoa(atob(str)) === str;
        } catch (e) {
            return false;
        }
    };
    that.isJSONString = function (str) {
        try {
            if (phpjs.is_string(str)) {
                var o = phpjs.json_decode(str);
                if (o && phpjs.is_object(o)) {
                    return true;
                }
            }
        } catch (e) {
            return false;
        }
        return false;
    };

    that.isExternalLink = function (url) {
        return phpjs.strpos(url, 'http://') !== false || phpjs.strpos(url, 'https://') !== false;
    };
    that.getClass = Function.prototype.call.bind(Object.prototype.toString);
    that.removeHostFromUrl = function (url) {
        if (this.isExternalLink(url)) {
            var parts = url.split('/');
            var pfx = parts[0] + '/' + parts[1] + '/' + parts[2] + '/';
            url = phpjs.str_replace(pfx, '/', url);
        }
        return url;
    };
    that.redirect = function (url) {
        window.location = url;
    };
    that.dateFormat = function () {
        return WC.params.get("dataformat", "Y-m-d H:i:s");
    };
    that.imagePlaceholder = function (width, height, text) {
        var w = width || 150;
        var h = height || 150;
        var t = text ? '&text=' + text : '';
        return '<img src="//via.placeholder.com/' + w + 'x' + h + t + '"/>';
    };
    that.syntaxHighlight = function (json) {
        if (phpjs.is_string(json)) {
            json = JSON.stringify(json, undefined, 2);
        }
        json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
            var cls = 'number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'key';
                } else {
                    cls = 'string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'boolean';
            } else if (/null/.test(match)) {
                cls = 'null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        });
    };
    that.fromDataConfigValue = function (o) {
        if (o && phpjs.is_string(o)) {
            if (that.isBase64Encoded(o)) {
                o = phpjs.base64_decode(o);
                if (that.isJSONString(o)) {
                    o = phpjs.json_decode(o);
                }
            }
            else if (that.isJSONString(o)) {
                o = phpjs.json_decode(o);
            }
        }
        else if (phpjs.is_object(o) && o.attr(WC.constants.ATTR_DATA_CONFIG)) {
            return that.fromDataConfigValue(o.attr(WC.constants.ATTR_DATA_CONFIG));
        }
        return o;
    };
    that.toDataConfigValue = function (o) {
        if (phpjs.is_object(o)) {
            return phpjs.base64_encode(phpjs.json_encode(o));
        }
        else if (phpjs.is_string(o) && that.isJSONString(o)) {
            return phpjs.base64_encode(o);
        }
        return o;
    };
    that.search = function ($variable, $context) {
        var $value = null;
        if ($variable && (phpjs.is_array(data) || phpjs.is_object(data))) {
            if (phpjs.is_string($variable)) {
                $variable = $variable.split(".");
            }
            for (var $key in $variable) {
                var $var = $variable[$key];
                if (phpjs.is_array($context)) {
                    if (phpjs.isset($context[$var])) {
                        $value = $context[$var];
                        $context = $value;
                    }
                    else {
                        $value = null;
                    }
                }
                else if ($context instanceof WC.contextModel) {
                    if ($context.has($var)) {
                        $value = $context.get($var);
                        $context = $value;
                    }
                    else {
                        $value = null;
                    }
                }
                else {
                    $value = null;
                }
            }
        }
        return $value;
    };

    that.applySortable = function (selector) {
        var e = $(selector);
        if (!WC.isHooked(e, 'sortable')) {
            e.sortable({
                group: phpjs.uniqid('no-drop-'),
                handle: '.fa-arrows-alt',
                onDragStart: function ($item, container, _super) {
                    if (!container.options.drop) {
                        $item.clone().insertAfter($item);
                    }
                    _super($item, container);
                }
            });
        }
    };

    that.applyColorPicker = function (selector) {
        var e = $(selector);
        if (!WC.isHooked(e, 'colorpicker')) {
            e.colorpicker({
                inline: true,
                container: true,
                extensions: [{
                    name: 'swatches',
                    options: {
                        colors: {
                            'tetrad1': '#000',
                            'tetrad2': '#000',
                            'tetrad3': '#000',
                            'tetrad4': '#000'
                        },
                        namesAsValues: false
                    }
                }]
            }).on('colorpickerChange colorpickerCreate', function (e) {
                var colors = e.color.generate('tetrad');
                colors.forEach(function (color, i) {
                    var colorStr = color.string();
                    var swatch = e.colorpicker.picker.find('.colorpicker-swatch[data-name="tetrad' + (i + 1) + '"]');
                    if (swatch.length) {
                        swatch.attr('data-value', colorStr).attr('title', colorStr).find('> i').css('background-color', colorStr);
                    }
                });
            });
        }
    };

    that.applyDatePicker = function (selector, settings) {
        var e = $(selector);
        if (!WC.isHooked(e, 'datepicker')) {
            e.datepicker(settings || {
                format: 'yyyy-mm-dd',
                startDate: '-3d',
                defaultDate: ''
            });
        }
    };

    that.applyDateTimePicker = function (selector, settings) {
        var e = $(selector);
        if (!WC.isHooked(e, 'datetimepicker')) {
            e.each(function(){
                var $t = $(this);
                var $id = phpjs.uniqid('dt-');
                var $input = $t.find('>input[type="text"]');
                $t.attr('id', $id);
                $input.attr('data-target', '#'+$id);
                $t.find('>div.input-group-append').attr('data-target', '#'+$id).attr('data-toggle', 'datetimepicker');
                if ($input.val() === '0') {
                    $input.attr('value', '0000-00-00 00:00:00');
                }
                $t.datetimepicker(settings || {
                    format: 'YYYY-MM-DD hh:mm:ss',
                    startDate: '-3d',
                    useCurrent: false,
                    icons: {
                        time: "fas fa-clock",
                        date: "fa fa-calendar",
                        up: "fa fa-arrow-up",
                        down: "fa fa-arrow-down"
                    }
                });
            });
        }
    };

    that.applyRichText = function (selector) {if (phpjs.is_callable('rteStandardConfig')) {rteStandardConfig(selector);}};

    that.applyMoreLess = function(selector) {
        var ele = $(selector);
        if (!WC.isHooked(ele, 'moreless')) {
            ele.each(function(){
                var e = $(this);
                var id = null;
                if (!e.find('.show.m').length) {
                    var words = e.html().split(' ');
                    var words1 = [], words2 = [];
                    for (var i = 0 ; i < words.length; i++) {
                        if (i < 40) {words1.push(words[i]);}
                        else {words2.push(words[i]);}
                    }
                    var html = [];
                    html.push(words1.join(' '));
                    if (words2.length) {
                        id = phpjs.uniqid('ml-');
                        html.push('<span class="hide">'+words2.join(' ')+'</span>');
                        html.push('<a href="javascript:void(0)" id="'+id+'">');
                        html.push('<span class="show m">'+WC.i18n.get('show_more')+'</span><span class="show l hide">'+WC.i18n.get('show_less')+'</span>');
                        html.push('</a>');
                    }
                    e.html(html.join(''));
                }
                else {
                    id = e.find('a').attr('id');
                    e.find('.show.m').html(WC.i18n.get(e.find('.show.m').html()));
                    e.find('.show.l').html(WC.i18n.get(e.find('.show.l').html()));
                }
                if (id) {
                    $('#'+id).click(function(){
                        var $this = $(this);
                        $this.find('.m').toggleClass('hide');
                        $this.find('.l').toggleClass('hide');
                        $this.prev().toggleClass('hide');
                    });
                }
            });
        }
    };

    that.applyLightBox = function(selector) {
        var e = $(selector||'[data-toggle="lightbox"]');
        if (e.length) {
            e.each(function(){
                var $t = $(this);
                if (!WC.isHooked($t, 'lightbox')) {
                    $t.on('click', function(event){
                        event.preventDefault();
                        $(this).ekkoLightbox();
                        return false;
                    });
                }
            });
        }
    };

    that.childOf = function(v) {
        var e = $('[data-child-of]');
        if (e.length) {
            if (v) {
                e.each(function(){
                    var $this = $(this);
                    if ($this.attr('data-child-of') === v) {
                        $this.show();
                    }
                    else {
                        $this.hide();
                    }
                });
            }
            else {
                e.each(function(){$(this).hide();});
            }
        }
    };

    that.str2fn = function(str) {
        var fn = null;
        if (str) {
            var parts = str.split('.');
            for (var i in parts) {
                if (window[parts[i]]) {
                    if (!fn) {
                        fn = window[parts[i]];
                    }
                }
                else if (fn && fn[parts[i]]) {
                    fn = fn[parts[i]];
                }
                else {
                    fn = null;
                }
            }
        }
        return fn;
    };

    return that;
}(jQuery);