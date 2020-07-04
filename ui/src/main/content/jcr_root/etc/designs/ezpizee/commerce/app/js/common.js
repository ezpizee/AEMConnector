var WCConfig = WCConfig||{};
var WC = function($) {
    var that = {};
    var hbsTmpls = {};
    that.hasHbsTmpl = function(t) {return typeof hbsTmpls[t] !== 'undefined';};
    that.hbsTmpl = function(t) {
        if (hbsTmpls[t]) {return hbsTmpls[t];}
        else {
            var s = '[data-hbstmpl="'+t+'"]', e = $(s);
            if (e.length) {
                hbsTmpls[t] = e.html();
                return hbsTmpls[t];
            }
        }
        console.error('Handlebars template: ' + t + ' does not exist.');
        return '';
    };
    that.scanHbsTmpl = function() {
        $('[data-hbstmpl]').each(function(){
            var e = $(this), t = e.attr('data-hbstmpl');
            if (t && !that.hasHbsTmpl(t)) {
                hbsTmpls[t] = e.html();
            }
        });
    };
    that.csrfTokenName = WCConfig.csrfTokenName;
    that.csrfTokenValue = WCConfig.csrfTokenValue||'';
    that.bindCSRFTokenToAjaxCalls = function() {
        $.ajaxSetup({
            beforeSend: function (xhr) {
                if (WC.csrfTokenName && WC.csrfTokenValue) {
                    xhr.setRequestHeader(WC.csrfTokenName, WC.csrfTokenValue);
                }
            }
        });
    };
    return that;
}(jQuery);

WC.callbacks = function($) {
    var that = {}, okBtnClick = {};
    that.onModalOKButtonClick = function (f, id) {
        if (phpjs.is_callable(f)) {
            okBtnClick[id] = f;
        }
    };
    that.getCallbacksForModalOKButtonClick = function(id){
        if (id != null) { return okBtnClick[id] || function() {}; }
        else { return okBtnClick; }
    };
    return that;
}(jQuery);

WC.params = function() {
    var that = {};
    var data = {};
    data.timeout = 2000;
    that.merge = function(o) {for (var i in o) {data[i] = o[i];}};
    that.get = function (k, v) {return data[k]||v;};
    that.has = function(k) {return !!data[k];};
    that.set = function(k, v) {data[k] = v;};
    that.remove = function(k) {if(that.has(k)){delete data[k];}};
    return that;
}();

WC.compileHandlebars = function(source, context) {
    try {
        if (WC.hasHbsTmpl(source)) {source = WC.hbsTmpl(source);}
        context = phpjs.is_object(context)||phpjs.is_array(context)?context:{};
        context.clientlibAssetRoot = WCConfig.clientlibAssetRoot||'clientlibs/commerce/app/images/';
        if (!context.clientlibAssetRoot.startsWith('http') && context.clientlibAssetRoot.startsWith('/')) {
            context.clientlibAssetRoot = phpjs.substr(context.clientlibAssetRoot, 1);
        }
        return Handlebars.compile(source)(context);
    }
    catch (e) {
        console.log(e);
        return e.message;
    }
};

WC.filterList = function(e){
    var $this = $(e), value = $this.val(), ul = $this.parent().next('ul');
    if (value) {
        if (ul.length) {
            ul.find('>li').each(function() {
                if ($(this).text().toLowerCase().search(value.toLowerCase()) > -1) {$(this).show();}
                else {$(this).hide();}
            });
        }
        else {ul.find('>li').show();}
    }
    else {
        ul.find('>li').show();
    }
};

WC.aem = function() {
    if (typeof WCConfig.aemCSRFTokenPath !== "undefined") {
        updateToken();
        // load every 10 minutes
        setInterval(updateToken, 100000);
    }
    function updateToken() {
        $.ajax({
            url: WCConfig.aemCSRFTokenPath,
            dataType: 'json',
            success: function(data) {
                WC.csrfTokenName = WCConfig.csrfTokenName;
                if (data && data.token) {
                    WC.csrfTokenValue = data.token;
                    var field = $('[name="'+WC.csrfTokenName+'"]');
                    if (field.length) {
                        field.attr('value', data.token);
                    }
                }
            }
        });
    }
};

// use for deleting user's installed app
WC.deleteApp = function(element, endpoint, hashedAppName) {
    if (hashedAppName) {
        var e = WC.renderModal(WC.i18n.get('LABEL_CONFIRM_DELETE'), WC.i18n.get('LABEL_CONFIRM_DELETE_MESSAGE'));
        var tmpId = phpjs.uniqid('modal-');
        e.find('>.modal:first-child').attr('id', tmpId);
        $('#'+tmpId).modal('show');

        WC.onOKClick(tmpId, onOk);

        function onOk() {
            WC.httpClient({
                type: 'POST',
                url: WC.constants.DELETE,
                data: {hashedAppName: hashedAppName, endpoint: endpoint},
                success: function (data) {
                    if (phpjs.sizeof(data) && data.message) {
                        WC.renderAlert('info', data.message);
                        if (data.status) {
                            element.remove();
                        }
                    }
                },
                error: function (a, b, c) {
                    WC.renderAlert('error', 'Error. ' + b + '; '+ c);
                }
            });
        }
    }
};

// use for deleting commerce items
WC.delete = function(endpoint, id) {
    if (endpoint && id) {
        var e = WC.renderModal(WC.i18n.get('LABEL_CONFIRM_DELETE'), WC.i18n.get('LABEL_CONFIRM_DELETE_MESSAGE'));
        var tmpId = phpjs.uniqid('modal-');
        e.find('>.modal:first-child').attr('id', tmpId);
        $('#'+tmpId).modal('show');

        WC.onOKClick(tmpId, onOk);

        function onOk() {
            WC.httpClient({
                type: 'POST',
                url: WC.constants.DELETE,
                data: {endpoint: endpoint, id: id},
                success: function (data) {
                    if (phpjs.sizeof(data) && data.message) {
                        WC.renderAlert('info', data.message);
                        setTimeout(function(){WC.loadCurrentPageContentInto(WC.ids.commerceItemsListId());},WC.params.get('timeout', 3000));
                    }
                },
                error: function (a, b, c) {
                    WC.renderAlert('error', 'Error. ' + b + '; '+ c);
                }
            });
        }
    }
    else {
        console.error('Endpoint and id are required, but missing.');
    }
};

WC.renderAlert = function(type, message, containerId) {
    var alert = WC.compileHandlebars(WC.hbsTmpl(WC.constants.HBS_ALERT_MESSAGE), {type: type, message: message});
    var e = WC.getElementById(containerId||WC.ids.alertContainerId());
    e.html(alert);
    setTimeout(function(){
        WC.getElementById(containerId||WC.ids.alertContainerId()).fadeOut();
    }, WC.params.get('timeout', 3000));
    return e;
};

WC.getElementById = function(id, isPrepend) {
    var e = $('#'+id);
    if (!e.length) {
        if (isPrepend) {
            $('body').prepend('<div id="'+id+'" class="'+id+'"></div>');
        }
        else {
            $('body').append('<div id="'+id+'" class="'+id+'"></div>');
        }
        e = $('#'+id);
    }
    return e;
};

WC.loadCurrentPageContentInto = function(containerId) {
    var uri = new WC.uri();
    var url = uri.path+(uri.queryString?'?':'')+uri.queryString;/*WC.params.get('currentPath');*/
    if (url) {
        let parts = url.split('#!');
        url = parts[parts.length-1];
        WC.loadPage(url);
    }
};

WC.loadPage = function(uri, callback) {
    if (uri) {
        var data = {}, dataType = 'json', isHtml = false;
        if (phpjs.strpos(uri, '.html') !== false) {
            dataType = 'html';
            isHtml = true;
        }
        WC.httpClient({
            url: uri,
            data: data,
            dataType: dataType,
            success: function(resp) {
                if (isHtml) {
                    $('#commerce-spa').html(resp);
                }
                else if (resp.display_template) {
                    resp.display_template = resp.display_template.replace('.hbs', '');
                    $('#commerce-spa').html(WC.compileHandlebars(WC.hbsTmpl(resp.display_template), resp));
                }
                else {
                    $('#commerce-spa').html(uri);
                }
                if (callback && phpjs.is_callable(callback)) {
                    callback(resp);
                }
            }
        });
    }
};

WC.isHooked = function(e, s) {
    if (e.length && !e.attr('data-hooked-'+s)) {
        e.attr('data-hooked-'+s, true);
        return false;
    }
    return true;
};

WC.unHook = function(e, s) {
    if (e.length && e.attr('data-hooked-'+s)) {
        e.removeAttr('data-hooked-'+s);
    }
};