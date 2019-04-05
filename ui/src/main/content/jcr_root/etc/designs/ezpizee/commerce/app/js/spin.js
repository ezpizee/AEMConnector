WC.spinner = function () {
    var that = {};
    var spinObject = null;
    var isRemoveAfterStop = false;
    var target = null;
    var options = {
        lines: 13 // The number of lines to draw
        ,length: 28 // The length of each line
        ,width: 14 // The line thickness
        ,radius: 52 // The radius of the inner circle
        ,scale: 2 // Scales overall size of the spinner
        ,corners: 1 // Corner roundness (0..1)
        ,color: '#666' // #rgb or #rrggbb or array of colors
        ,opacity: 0.25 // Opacity of the lines
        ,rotate: 0 // The rotation offset
        ,direction: 1 // 1: clockwise, -1: counterclockwise
        ,speed: 1 // Rounds per second
        ,trail: 60 // Afterglow percentage
        ,fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
        ,zIndex: 2e9 // The z-index (defaults to 2000000000)
        ,className: 'spinner' // The CSS class to assign to the spinner
        ,top: '50%' // Top position relative to parent
        ,left: '50%' // Left position relative to parent
        ,shadow: false // Whether to render a shadow
        ,hwaccel: false // Whether to use hardware acceleration
        ,position: 'absolute'
    };
    that.start = function () {
        if (typeof Spinner !== "undefined") {
            var arg = arguments[0]||'body';
            if (arg === null) {
                arg = WC.getElementById(WC.ids.activityIndicatorId());
                if (arg && arg.length) {
                    arg = arg[0];
                }
            }
            isRemoveAfterStop = (arguments[1]||'') === 'remove';
            target = phpjs.is_string(arg) ? WC.getElementById(arg) : arg;
            if (target && target.length) {
                spinObject = new Spinner(options).spin(target[0]);
            }
        }
        else {
            console.error("Spinner is undefined");
        }
        return spinObject;
    };
    that.stop = function() {
        if (spinObject) {
            spinObject.stop();
        }
        var e = $('.spinner[role="progressbar"]');
        if (e.length) {
            e.remove();
        }
        if (isRemoveAfterStop && target) {target.remove();}
    };
    return that;
}();