WC.ids = function () {
    var that = {};
    that.modalContainerId = function() {return WC.params.get('modalContainerId')||'modal-container';};
    that.alertContainerId = function() {return WC.params.get('alertContainerId')||'alert-container';};
    that.activityIndicatorId = function() {return WC.params.get('activityIndicatorId')||'activity-indicator';};
    that.commerceItemsListId = function() {return WC.params.get('commerceItemsListId')||'commerce-itemsList';};
    return that;
}();