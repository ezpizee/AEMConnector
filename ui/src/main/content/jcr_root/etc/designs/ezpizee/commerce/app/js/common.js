var WC = function() {
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
    that.bindCSRFTokenToAjaxCalls = function() {
        $.ajaxSetup({
            beforeSend: function (xhr) {
                if (csrftoken !== undefined) {
                    xhr.setRequestHeader("csrftoken", csrftoken);
                }
            }
        });
    };
    return that;
}();

WC.callbacks = function() {
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
}();

WC.params = function() {
    var that = {};
    var data = {};
    that.merge = function(o) {for (var i in o) {data[i] = o[i];}};
    that.get = function (k, v) {return data[k]||v;};
    that.has = function(k) {return !!data[k];};
    that.set = function(k, v) {data[k] = v;};
    that.remove = function(k) {if(that.has(k)){delete data[k];}};
    return that;
}();

WC.compileHandlebars = function(source, context) {
    try {
        return Handlebars.compile(source)(phpjs.is_object(context) || phpjs.is_array(context) ? context : {});
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

WC.deleteApp = function(element, endpoint, hashedAppName) {
    if (hashedAppName) {
        var e = WC.renderModal(WC.i18n.get('LABEL_CONFIRM_DELETE'), WC.i18n.get('LABEL_CONFIRM_DELETE_MESSAGE'));
        var tmpId = phpjs.uniqid('modal-');
        e.find('>.modal:first-child').attr('id', tmpId);
        $('#'+tmpId).modal('show');
        WC.onOKClick(tmpId, onOk);
        function onOk(modalId) {
            WC.httpClient({
                type: 'DELETE',
                url: WC.constants.DELETE,
                data: {hashedAppName: hashedAppName, endpoint: endpoint},
                success: function (data) {
                    console.log(data);
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

WC.delete = function(endpoint, id) {
    if (endpoint && id) {
        var e = WC.renderModal(WC.i18n.get('LABEL_CONFIRM_DELETE'), WC.i18n.get('LABEL_CONFIRM_DELETE_MESSAGE'));
        var tmpId = phpjs.uniqid('modal-');
        e.find('>.modal:first-child').attr('id', tmpId);
        $('#'+tmpId).modal('show');

        WC.onOKClick(tmpId, onOk);

        function onOk(modalId) {
            WC.httpClient({
                type: 'DELETE',
                url: WC.constants.DELETE,
                data: {endpoint: endpoint, id: id},
                success: function (data) {
                    if (phpjs.sizeof(data) && data.message) {
                        WC.renderAlert('info', data.message);
                        WC.loadCurrentPageContentInto(WC.ids.commerceItemsListId());
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

WC.loadPage = function(uri) {
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
            }
        });
    }
};