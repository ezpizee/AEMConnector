WC.observers = function() {
    var that = {};
    var onWindowResizeCallbacks = {};
    var onHashChangeCallbacks = {};
    var onElementChangeCallbacks = {};
    var observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.target) {
                if (mutation.target.tagName && onElementChangeCallbacks[mutation.target.tagName.toLowerCase()]) {
                    var eName = mutation.target.tagName.toLowerCase();
                    for (var k in onElementChangeCallbacks[eName]) {
                        onElementChangeCallbacks[eName][k]['callback'](onElementChangeCallbacks[eName][k]['args']);
                    }
                }
                else if (mutation.target.id) {
                    var eName = mutation.target.id.toLowerCase();
                    for (var k in onElementChangeCallbacks[eName]) {
                        onElementChangeCallbacks[eName][k]['callback'](onElementChangeCallbacks[eName][k]['args']);
                    }
                }
            }
        });
    });
    var observerConfig = {
        attributes: true,
        childList: false,
        characterData: true
    };

    that.win = $(window);
    that.hash = window.location.hash;

    that.init = function() {onWindowResize();onHashChange();};

    that.bindOnWindowResize = function(name, callback, args) {
        if (!onWindowResizeCallbacks[name] && phpjs.is_callable(callback)) {
            onWindowResizeCallbacks[name] = {callback: callback, args: args};
        }
    };
    that.bindOnHashChange = function(name, callback, args) {
        if (!onHashChangeCallbacks[name] && phpjs.is_callable(callback)) {
            onHashChangeCallbacks[name] = {callback: callback, args: args||null};
        }
    };

    that.unbindOnWindowResize = function(name){if (onWindowResizeCallbacks[name]) {delete onWindowResizeCallbacks[name];}};
    that.unbindOnHashChange = function(name){if (onHashChangeCallbacks[name]) {delete onHashChangeCallbacks[name];}};

    /**
     * For binding callback function to HTML DOM element changes
     *
     * @param eName element name
     * @param name human readable name of the callback function
     * @param callback callable/callback function
     * @param args any arguments that it might need
     */
    that.bindOnElementChange = function(eName, name, callback, args) {
        if (phpjs.is_callable(callback) && eName && name) {
            var e;
            if (phpjs.is_string(eName)) {
                if (document[eName]) {
                    e = document[eName];
                    observerConfig.childList = false;
                }
                else if (document.getElementById(eName)) {
                    e = document.getElementById(eName);
                    observerConfig.childList = true;
                }
                else if (eName.startsWith('#')) {
                    eName = eName.replace('#', '');
                    e = document.getElementById(eName);
                    observerConfig.childList = true;
                }
            }
            else if (eName[0]) {
                e = eName[0];
                eName = eName.attr('id')||phpjs.md5(eName.html());
            }
            else {
                e = eName;
                eName = eName.id||phpjs.uniqid();
            }
            if (e) {
                if (!onElementChangeCallbacks[eName]) {
                    onElementChangeCallbacks[eName] = {};
                    observer.observe(e, observerConfig);
                }
                onElementChangeCallbacks[eName.toLowerCase()][name] = {callback: callback, args: args};
            }
        }
    };
    that.unbindOnElementChange = function(eName, name){
        if (eName && name && onElementChangeCallbacks[eName.toLowerCase()] && onElementChangeCallbacks[eName.toLowerCase()][name]) {
            delete onElementChangeCallbacks[eName][name];
        }
    };

    function onWindowResize() {
        $(window).resize(function(){
            that.win = $(window);
            for (var name in onWindowResizeCallbacks) {
                onWindowResizeCallbacks[name]['callback'](onWindowResizeCallbacks[name]['args']);
            }
        });
    }
    function onHashChange() {
        $(window).on('hashchange', function(){
            that.hash = window.location.hash;
            for (var name in onHashChangeCallbacks) {
                onHashChangeCallbacks[name]['callback'](onHashChangeCallbacks[name]['args']);
            }
        });
    }

    return that;
}();