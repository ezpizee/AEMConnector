WC.assets = function() {
    var that = {}, editBtn = null, deleteBtn = null, createBtn = null;
    var containerSelector = '.commerce-items.assets';
    var columnsSelector = '.panel-body-content-columns .column';
    var assetCookieName = 'da_selected_folder';
    that.loadDrilldown = function(itemId, container) {
        remember(itemId, container);
        container = jQuery(container).parent()[0];
        removeExtraColumns(container);
        highlightClickedColumn(container);
        WC.httpClient({
            url: WC.constants.ASSETS_BY_PARENT,
            data: {parent_id: itemId},
            success: function (data) {
                if (phpjs.sizeof(data) && phpjs.sizeof(data.data)) {
                    var html = WC.compileHandlebars(WC.hbsTmpl('column'), {list: data.data});
                    container = jQuery(container);
                    var nextColumn = container.parent().parent().next('.column');
                    if (!nextColumn.length) {
                        container.parent().parent().parent().append('<div class="column"></div>');
                        nextColumn = container.parent().parent().parent().find('.column:last-child');
                    }
                    nextColumn.replaceWith(html);
                    autoScroll();
                }
            }
        });
    };
    that.loadContent = function(itemId, container) {
        container = jQuery(container).parent()[0];
        removeExtraColumns(container);
        highlightClickedColumn(container);
        WC.httpClient({
            url: WC.constants.ASSET_DATA,
            data: {id: itemId},
            success: function (data) {
                container = jQuery(container);
                var nextColumn = container.parent().parent().next('.column');
                if (!nextColumn.length) {
                    container.parent().parent().parent().append('<div class="column"></div>');
                    nextColumn = container.parent().parent().parent().find('.column:last-child');
                }
                var html = WC.compileHandlebars(WC.hbsTmpl('preview'), {item_data: data.data});
                nextColumn.replaceWith(html);
                autoScroll();
            }
        });
    };
    that.fileUpload = function(e) {
        var e = jQuery(e);
        var file = e.next().next('[type="file"]');
        if (file.length) {
            file.click();
            file.change(function(){
                var $in = jQuery(this);
                e.prev().val($in.val());
            });
        }
    };
    that.buildFormUri = function(id, ele) {
        setBtns();
        var ele = jQuery(ele);
        editBtn.removeClass('hide');
        deleteBtn.removeClass('hide');
        var value = phpjs.json_decode(ele.val());
        if (value.type === 'folder') {
            editBtn.attr('href', href(editBtn.attr('data-folder-url'), 'edit_id='+value.id));
        }
        else if (value.type === 'file' || value.type === 'doc' || value.type === 'image'|| value.type === 'picture') {
            editBtn.attr('href', href(editBtn.attr('data-file-url'), 'edit_id='+value.id));
        }
        deleteBtn.attr('data-id', id);
        if (createBtn && createBtn.length) {
            createBtn.each(function(){
                var t = jQuery(this);
                var h = t.attr('href');
                var arr = h.split('?parent_id=');
                if (arr.length === 2) {
                    h = arr[1] + '?parent_id=' + id;
                }
                else {
                    h = h + '?parent_id=' + id;
                }
                t.attr('href', h);
            });
        }
    };

    that.init = function() {
        setBtns();
        var count = 0, intv = setInterval(function(){
            if (count >= 2000) {
                clearInterval(intv);
            }
            else if (jQuery(columnsSelector).length) {
                clearInterval(intv);
                autoClick();
            }
            count++;
        }, 10);
    };

    function autoScroll() {
        let e1 = jQuery(containerSelector);
        let e2 = jQuery(columnsSelector);
        let e3 = jQuery(columnsSelector+':first-child');
        if (e1.length && e2.length) {
            if (e1.outerWidth() < e2.length * e3.outerWidth()) {
                let leftPos = e1.scrollLeft();
                e1.animate({scrollLeft: leftPos + e3.outerWidth()}, 800);
            }
        }
    }

    function autoClick(cValues) {
        cValues = cValues||WC.cookie.get(assetCookieName);
        if (cValues) {
            var numColumns = jQuery(columnsSelector).length;
            for(var i in cValues) {
                if (cValues[i] !== undefined && cValues[i].index !== undefined) {
                    var element = jQuery('[data-folder="'+cValues[i].folder_id+'"]');
                    if (element.length) {
                        element.trigger('click');
                        cValues[i] = [];
                        var count = 0, intv = setInterval(function(){
                            if (count >= 1000) {
                                clearInterval(intv);
                            }
                            else if (numColumns < jQuery(columnsSelector).length) {
                                clearInterval(intv);
                                autoClick(cValues);
                            }
                            count++;
                        }, 100);
                    }
                }
            }
        }
    }

    function remember(id, container) {
        var column = jQuery(container).parent().parent().parent();
        if (column.hasClass('column')) {
            var index = column.index(), value = WC.cookie.get(assetCookieName);
            if (value) {
                var newValue = [];
                for (var i in value) {
                    if (parseInt(value[i].index) <= parseInt(index)) {
                        newValue.push(value[i]);
                    }
                }
                value = newValue;
                if (!contains(id, value)) {
                    value.push({index: index, folder_id: id});
                }
            }
            else {
                value = [{index: index, folder_id: id}];
            }
            WC.cookie.set({name: assetCookieName, value: value});
        }
    }

    function contains(id, value) {
        for(var i in value) {
            if (value[i].folder_id === id) {
                return true;
            }
        }
        return false;
    }

    function href(url, q) {return url + ((url?url.split("?"):[]).length > 1 ? '&' : '?') + q;}

    function removeExtraColumns(container) {
        container = jQuery(container).parent().parent();
        var index = container.index();
        var nextColumn = container.next('.column');
        if (nextColumn.length) {
            index = nextColumn.index();
        }
        container.parent().find('.column').each(function(i){
            if (i > index) {
                jQuery(this).remove();
            }
        });
    }

    function highlightClickedColumn(container) {
        removeAllHighlight();
        jQuery(container).addClass('is-active');
    }

    function removeAllHighlight() {jQuery(containerSelector).find('.column-item').each(function(){jQuery(this).removeClass('is-active');});}

    function setBtns() {
        editBtn = jQuery('[data-btn="edit"]');
        deleteBtn = jQuery('[data-btn="delete"]');
        createBtn = jQuery('[data-btn="create"]');
    }

    return that;
}();