"use strict";

var global = this;

use(function () {

    var _retrieveAttribute = function (attrName, defaultValue) {
        var value = defaultValue;
        if (global.request
            && global.request.getAttribute
            && global.request.getAttribute(attrName)) {
            value = global.request.getAttribute(attrName);
        }

        return value;
    };

    var _getEditContext = function () {
        var editContext = undefined;
        var componentContext = _retrieveAttribute("com.day.cq.wcm.componentcontext");
        if (componentContext) {
            editContext = componentContext.getEditContext();
        }

        return editContext;
    };

    var editContext = _getEditContext();

    if (editContext
        && editContext.getParent
        && editContext.getParent() != null) {
        var parentContext = editContext.getParent();

        var curRes = editContext.getParent().getAttribute("currentResource");
        if (curRes != null) {
            var prev = global.Packages.com.day.text.Text.getName(curRes.getPath());
            editContext.getEditConfig().setInsertBehavior("before " + prev);
        }
    }

    return {};
});
