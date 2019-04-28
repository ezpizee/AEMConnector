$(document).ready(function(){
    WC.observers.init();
    WC.formUtil.init();
    let uri = new WC.uri();
    let pfx = typeof adminPathPfx !== "undefined" ? adminPathPfx : '/commerce/';
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