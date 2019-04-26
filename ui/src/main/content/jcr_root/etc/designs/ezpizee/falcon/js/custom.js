jQuery(document).ready(function(){
    var alert = jQuery('[role="alert"]');
    if (alert.length) {setTimeout(function(){alert.fadeOut();}, 5000);}
    WC.formUtil.bindValidator(true);
    WC.bindCSRFTokenToAjaxCalls();
    var commerceAssets = $('#commerce-items-assets');
    if (commerceAssets.length) {
        var cardHeader = commerceAssets.find('.card .card-header');
        var cardBody = commerceAssets.find('.card .card-body');
        if (cardHeader.length && cardBody.length) {
            var contentNav = $('.content>nav.navbar');
            if (contentNav.length) {
                cardBody.height($(document).outerHeight() - (cardHeader.outerHeight() + contentNav.outerHeight() + 150));
            }
        }
    }
    signUpForm();
});