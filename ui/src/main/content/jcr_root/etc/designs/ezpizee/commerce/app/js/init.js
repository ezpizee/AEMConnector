$(document).ready(function(){
    WC.aem();
    WC.bindCSRFTokenToAjaxCalls();
    WC.observers.init();
    WC.formUtil.init();
    let uri = new WC.uri();
    let pfx = WCConfig.adminPathPfx||'/commerce/';
    if (uri.fragment && uri.fragment.startsWith(pfx)) {
        WC.loadPage(uri.fragment);
        $(window).on('hashchange', function(){
            uri = new WC.uri();
            if (uri.fragment && uri.fragment.startsWith(pfx)) {
                WC.loadPage(uri.fragment);
            }
        });
    }
    else {
        $(window).on('hashchange', function(){
            uri = new WC.uri();
            if (uri.fragment && uri.fragment.startsWith(pfx)) {
                WC.loadPage(uri.fragment);
            }
        });
    }
});