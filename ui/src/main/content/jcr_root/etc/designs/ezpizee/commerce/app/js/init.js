(function($){

    var pfx = WCConfig.adminPathPfx||'/commerce/';

    function isHTMLFragment(url) {return url.startsWith(pfx) && phpjs.strpos(url, '.html') !== false;}

    function toJSONFragment(url) {return phpjs.str_replace('.html', '.json', url);}

    function onHashChange() {
        $(window).on('hashchange', function(){
            uri = new WC.uri();
            if (uri.fragment && uri.fragment.startsWith(pfx)) {
                if (isHTMLFragment(uri.path)) {
                    uri.fragment = toJSONFragment(uri.fragment);
                    window.location = toJSONFragment(uri.path);
                }
                WC.loadPage(uri.fragment, init);
            }
        });
    }

    function init() {
        WC.aem();
        WC.bindCSRFTokenToAjaxCalls();
        WC.observers.init();
        WC.formUtil.init();
        var initElements = $('[data-init]');
        if (initElements.length) {
            initElements.each(function () {
                var $this = $(this), data = $this.attr('data-init');
                if (data && WC.utilities.isJSONString(data)) {
                    data = phpjs.json_decode(data);
                    var args = data.args||'', callback = WC.utilities.str2fn(data.callback);
                    if (callback && phpjs.is_callable(callback)) {
                        callback(args);
                    }
                }
            })
        }
    }

    $(document).ready(function(){
        init();
        let uri = new WC.uri();
        if (uri.fragment && uri.fragment.startsWith(pfx)) {
            if (isHTMLFragment(uri.path)) {
                uri.fragment = toJSONFragment(uri.fragment);
                window.location = toJSONFragment(uri.path);
            }
            WC.loadPage(uri.fragment, init);
            onHashChange();
        }
        else {
            onHashChange();
        }
    });
})(jQuery);