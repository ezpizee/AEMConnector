WC.commonCountryModal = function(o) {WC.commonModal(o, WC.constants.COMMON_CONTRY_LIST, 'country');};
WC.commonCurrencyModal = function(o) {WC.commonModal(o, WC.constants.COMMON_CURRENCY_LIST, 'currency');};
WC.currencyModal = function(o){WC.commonModal(o, WC.constants.CURRENCY_LIST, 'currency');};
WC.countryModal = function(o){WC.commonModal(o, WC.constants.COUNTRY_LIST, 'country');};
WC.lengthModal = function(o){WC.commonModal(o, WC.constants.LENGTH_LIST, 'length');};
WC.temperatureModal = function(o){WC.commonModal(o, WC.constants.TEMPERATURE_LIST, 'temperature');};
WC.weightModal = function(o){WC.commonModal(o, WC.constants.WEIGHT_LIST, 'weight');};
WC.addressModal = function(o){WC.commonModal(o, WC.constants.ADDRESS_LIST, 'address');};
WC.categoryModal = function(o, ele){WC.commonModal(o, WC.constants.CATEGORY_LIST, 'category', ele);};
WC.categoryParentModal = function(o){WC.commonModal(o, WC.constants.CATEGORY_LIST, 'category_parent');};
WC.paymentServiceModal = function(o){WC.commonModal(o, WC.constants.COMMON_PAYMENT_SERVICE_LIST, 'payment_service');};
WC.paymentMethodModal = function(o){WC.commonModal(o, WC.constants.PAYMENT_METHOD_LIST, 'payment_method');};
WC.storeModal = function(o, ele){WC.commonModal(o, WC.constants.STORE_LIST, 'store', ele);};
WC.productTypeModal = function(o){WC.commonModal(o, WC.constants.PRODUCT_TYPE_LIST, 'product_type');};
WC.productModal = function(o){WC.commonModal(o, WC.constants.PRODUCT_LIST, 'product');};
WC.productOneModal = function(o){WC.commonModal(o, WC.constants.PRODUCT_LIST, 'product_1');};
WC.productTwoModal = function(o){WC.commonModal(o, WC.constants.PRODUCT_LIST, 'product_2');};
WC.fileModal = function(o, ele){WC.assetModal(o, WC.constants.ASSETS_CONTENT, 'file', ele);};
WC.folderModal = function(o) {WC.assetModal(o, WC.constants.FOLDER_CONTENT, 'folder');};

WC.assetModal = function(o, url, fieldName, $ele) {
    WC.httpClient({
        url: url,
        dataType: 'html',
        success: function (html) {
            var e = $('<div>'+html+'</div>');
            html = '<div class="commerce-items assets" id="commerce-items-list">'+e.find('#commerce-items-list').html()+'</div>';
            e.find('#commerce-items-assets').html(html);
            e = WC.renderModal(o.title||WC.i18n.get('LABEL_FILE'), e.html());
            var tmpId = phpjs.uniqid('modal-');
            e.find('>.modal:first-child').attr('id', tmpId);
            var modalElement = $('#'+tmpId);
            modalElement.modal('show');
            modalElement.find('.modal-dialog').addClass('modal-xl');
            modalElement.find('.panel-header').remove();
            modalElement.find('.commerce-items.assets').css({minHeight: '400px'});
            WC.onOKClick(tmpId, onOk, ['productTypeAttrsFieldset']);
        }
    });
    function onOk(modalId) {
        var e = $('#'+modalId+' input[name="asset"]');
        e.each(function(){
            var input = $(this);
            if (input.is(':checked')) {
                var obj = phpjs.json_decode(input.val());
                var nameField = [], idField = [];
                if ($ele) {
                    $ele = $($ele);
                    nameField = $ele.prev('[data-field="'+fieldName+'_name"]');
                    idField = $ele.next('[data-field="'+fieldName+'_id"]');
                }
                else {
                    nameField = $('#'+fieldName+'_name');
                    idField = $('#'+fieldName+'_id');
                    if (!nameField.length) {nameField = $('input[data-field="'+fieldName+'_name"]');}
                    if (!idField.length) {idField = $('input[data-field="'+fieldName+'_id"]');}
                    if (idField.length) {$ele = idField.prev();}
                }
                if (idField.length) {idField.attr('value', obj.id);}
                if (nameField.length) {nameField.attr('value', obj.title||obj.name);}
                $('#'+modalId).modal('hide');

                if ($ele && $ele.length && obj.path) {
                    if (obj.is_image) {
                        var content = [];
                        content.push('<a href="'+obj.path+'" data-toggle="lightbox">');
                        content.push('<img src="'+obj.path+'" alt="Preview of '+obj.path+'" class="rounded img-thumbnail" />');
                        content.push('</a>');
                        $ele.parent().next().html(content.join(''));
                        WC.utilities.applyLightBox();
                    }
                    else if (obj.is_file) {
                        $ele.parent().next().next().html('<a href="'+obj.path+'">'+obj.attrs.name+'</a>');
                    }
                }
            }
        });
    }
};

WC.commonModal = function(o, url, fieldName, $ele) {
    WC.httpClient({
        url: url,
        success: function (data) {
            var html, e, tmpId;
            if (phpjs.sizeof(data)) {
                html = WC.compileHandlebars(WC.hbsTmpl(WC.constants.HBS_RADIO_BUTTON_LIST), {
                    data: (data&&data.data?data.data:[]),
                    fieldName: fieldName,
                    showFilterFiled: true,
                    csrftoken: WC.params.get('csrftoken')
                });
                e = WC.renderModal(o.title||fieldName, html);
                tmpId = phpjs.uniqid('modal-');
                e.find('>.modal:first-child').attr('id', tmpId);
                var modalElement = $('#'+tmpId);
                modalElement.modal('show');
                WC.onOKClick(tmpId, onOk);
                var fieldElement = $('[data-field="'+fieldName+'_id"]');
                if (!fieldElement.length) {fieldElement = $('#'+fieldName+'_id');}
                if (fieldElement.length) {
                    var currentVal = fieldElement.val();
                    if (currentVal) {
                        var inputField = modalElement.find('input[value="'+currentVal+'"]');
                        console.log(currentVal);
                        if (inputField.length) {
                            inputField.trigger('click');
                            $('.modal-body-content', modalElement).animate({
                                scrollTop: inputField.parent().parent().offset().top
                            }, 2000);
                        }
                    }
                }
            }
            else {
                e = WC.renderModal(o.title||fieldName, WC.i18n.get('NO_RESULTS'));
                tmpId = phpjs.uniqid('modal-');
                e.find('>.modal:first-child').attr('id', tmpId);
                $('#'+tmpId).modal('show');
                $('#'+tmpId+' [data-btn="ok"]').hide();
            }
        }
    });
    function onOk(modalId) {
        var e = $('#'+modalId+' input[name="'+fieldName+'"]');
        e.each(function(){
            var input = $(this);
            if (input.is(':checked')) {
                var obj = phpjs.json_decode(input.attr('data-object'));
                var nameField = [], idField = [];
                if ($ele) {
                    $ele = $($ele);
                    nameField = $ele.prev('[data-field="'+fieldName+'_name"]');
                    idField = $ele.next('[data-field="'+fieldName+'_id"]');
                }
                else {
                    nameField = $('#'+fieldName+'_name');
                    idField = $('#'+fieldName+'_id');
                    if (!nameField.length) {nameField = $('input[data-field="'+fieldName+'_name"]');}
                    if (!idField.length) {idField = $('input[data-field="'+fieldName+'_id"]');}
                }
                if (idField.length) {idField.attr('value', obj.id);}
                if (nameField.length) {nameField.attr('value', obj.name||obj.title);}
                $('#'+modalId).modal('hide');
            }
        });
    }
};

WC.onOKClick = function(modalId, callback, callbacksToSkip) {
    if (modalId && phpjs.is_callable(callback)) {
        var okBtn = $('#'+modalId+' [data-btn="ok"]');
        if (okBtn.length) {
            okBtn.click(function(){
                if (phpjs.is_callable(callback)) {callback(modalId);}
                $('#'+modalId).modal('hide');
                var callbackHash = WC.callbacks.getCallbacksForModalOKButtonClick();
                for (var callbackId in callbackHash) {
                    if (callbacksToSkip == null || !callbacksToSkip.includes(callbackId)) {
                        WC.callbacks.getCallbacksForModalOKButtonClick(callbackId)(modalId);
                    }
                }
            });
        }
    }
};

WC.renderModal = function(title, content) {
    var modal = WC.compileHandlebars(WC.hbsTmpl(WC.constants.HBS_MODAL), {title: title, content: content});
    var e = WC.getElementById(WC.ids.modalContainerId());
    e.html(modal);
    return e;
};