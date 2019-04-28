WC.formUtil = function() {
    var that = {};

    that.toggleCategoryOrderingItem = function (val) {
        var orderingItemEle = $('#ordering_item_set');
        if (orderingItemEle.length) {
            if (!val) {
                var e = $('#ordering_position option:selected');
                val = e.length ? e.attr('value') : '';
            }
            if (val === 'before' || val === 'after') {
                orderingItemEle.show();
            }
            else {
                orderingItemEle.hide();
                $('#category_name').val('');
                $('#category_id').val('');
            }
        }
    };

    that.paymentServiceFieldset = function(modalId) {
        var payment_service_fieldset = $('#payment_service_fieldset');
        if (payment_service_fieldset.length && $('#'+modalId+' input[name="payment_service"]').length) {
            var v = that.getVal(modalId, 'payment_service');
            if (phpjs.sizeof(v)) {
                var html = WC.compileHandlebars(WC.hbsTmpl(WC.constants.HBS_PAYMENT_SERVICE[(v.id).toUpperCase()]), []);
                payment_service_fieldset.html(html);
            }
            else {
                payment_service_fieldset.html('');
            }
        }
    };

    that.getVal = function(modalId, fieldName) {
        var e = $('#'+modalId+' input[name="'+fieldName+'"]');
        var output = {};
        if (e.length) {
            e.each(function(){
                var input = $(this);
                if (input.is(':checked') && !output.length) {
                    output.id = input.val();
                    output.name = input.parent().find('>span').html();
                }
            });
        }
        return output;
    };

    that.init = function () {
        this.toggleCategoryOrderingItem();
        WC.formUtil.productManager.loadFieldType();
        WC.formUtil.productManager.productAttrsList();
        WC.callbacks.onModalOKButtonClick(WC.formUtil.paymentServiceFieldset, 'paymentServiceFieldset');
        WC.callbacks.onModalOKButtonClick(WC.formUtil.productManager.productTypeAttrsFieldset, 'productTypeAttrsFieldset');

        WC.utilities.applyRichText('textarea');
        WC.utilities.applyColorPicker('[data-trigger="colorpicker"]');
        WC.utilities.applySortable('[data-trigger="sortable"]');
        WC.utilities.applyLightBox('[data-trigger="lightbox"]');
        WC.utilities.applyDatePicker('[data-trigger="datepicker"]');
        WC.utilities.applyDateTimePicker('[data-trigger="datetimepicker"]');
        WC.formUtil.bindValidator();
        WC.bindCSRFTokenToAjaxCalls();
    };

    that.bindValidator = function(noAjaxSubmit) {
        let forms = jQuery('form');
        if (forms.length) {
            let ids = [];
            forms.each(function(){
                let e = jQuery(this);
                if (e.attr('id')) {
                    ids.push('#'+e.attr('id'));
                }
                else {
                    let id = phpjs.uniqid('ezpz-form-');
                    e.attr('id', id);
                    ids.push('#'+id);
                }
            });
            if (ids.length) {
                jQuery.validate({
                    form: ids.join(','),
                    modules : 'location, date, security, file',
                    errorMessagePosition: 'top',
                    showHelpOnFocus: false,
                    onModulesLoaded : function() {},
                    onSuccess: function($form) {
                        if (noAjaxSubmit) {
                            return true;
                        }
                        let oldAction = $form.attr('action');
                        let parts = oldAction.split('#!');
                        if (parts.length === 2) {
                            let action = parts[parts.length-1];
                            let parts2 = action.split('?');
                            $form.attr('action', action+(parts2.length>1?'&':'?')+'response=json');
                        }
                        // ajax submit the form via jquery.form
                        let spinner = WC.spinner.start();
                        $form.ajaxSubmit({
                            beforeSend: function(xhr) {
                                if (typeof csrftoken === "undefined") {
                                    var csrfTokenField = $form.find('[name="CSRF-Token"]');
                                    if (csrfTokenField.length) {
                                        xhr.setRequestHeader("CSRF-Token", csrfTokenField.val());
                                    }
                                    else {
                                        csrfTokenField = $form.find('[name="csrftoken"]');
                                        if (csrfTokenField.length) {
                                            xhr.setRequestHeader("csrftoken", csrfTokenField.val());
                                        }
                                    }
                                }
                                else {
                                    xhr.setRequestHeader("csrftoken", csrftoken);
                                }
                            },
                            success: function(resp) {
                                if (phpjs.is_string(resp)) {resp=phpjs.json_decode(resp)||{};}
                                if (typeof resp.success !== "undefined") {
                                    WC.renderAlert('success', resp.message||"Unknown message", formMsgId($form));
                                    if (typeof resp.data !== "undefined" && phpjs.is_object(resp.data) &&
                                        typeof resp.data.id !== "undefined" && phpjs.strpos(oldAction, 'edit_id='+resp.data.id) === false) {
                                        oldAction = oldAction + '?edit_id='+resp.data.id;
                                        location.href = oldAction;
                                    }
                                    else {
                                        $form.attr('action', oldAction);
                                    }
                                    if ($form.attr('data-onsubmit-success')) {
                                        $form.attr('data-onsubmit-success')(resp);
                                    }
                                }
                                else {
                                    WC.renderAlert('error', resp.message||"Unknown message", formMsgId($form));
                                    $form.attr('action', oldAction);
                                    if ($form.attr('data-onsubmit-error')) {
                                        $form.attr('data-onsubmit-error')(resp);
                                    }
                                }
                                spinner.stop();
                            },
                            error: function(a,b,c) {
                                spinner.stop();
                                WC.renderAlert('error', 'Error. '+c, formMsgId($form));
                            }
                        });
                        return false;
                    },
                    onError: function($form) {
                        if ($form.find('.form-error.alert').length) {
                            setTimeout(function(){
                                $form.find('.form-error.alert').fadeOut();
                            }, 4500);
                        }
                        return false;
                    }
                });
                aemCSRFToken();
            }
        }
    };

    function aemCSRFToken() {
        if (typeof csrftoken === "undefined" && typeof csrfTokenPath !== "undefined") {
            if ($('[name="CSRF-Token"]').length) {
                fetchCSRFToken(csrfTokenPath);
                setInterval(function() {fetchCSRFToken(csrfTokenPath);}, 100000);
            }
        }
    }

    function fetchCSRFToken(uri) {
        $.ajax({
            url: uri,
            dataType: 'json',
            success: function(data) {
                if (data && data.token) {
                    $('[name="CSRF-Token"]').attr('value', data.token);
                }
            }
        });
    }

    function formMsgId($form) {
        if (!$('#'+$form.attr('id')+'-msg').length) {
            $form.before('<div id="'+$form.attr('id')+'-msg"></div>');
        }
        return $form.attr('id')+'-msg';
    }

    return that;
}();

WC.formUtil.productManager = function(){
    var that = {};
    var field_type_set_selector = '#field_type_set', field_type_mandatory_fields_selector = '#field_type_mandatory_fields';
    var field_type_set = null, field_type_mandatory_fields = null;

    that.loadFieldType = function(selectedFieldType, e) {
        field_type_set = $(field_type_set_selector);
        field_type_mandatory_fields = $(field_type_mandatory_fields_selector);
        if (field_type_set.length) {
            e = e ? $(e) : $('#field_type');
            var field_type_title = '';
            if (!selectedFieldType && e.length) {
                var opt = e.find('option:selected');
                selectedFieldType = opt.val();
                field_type_title = opt.html();
            }
            else {
                field_type_title = e.find('option:selected').html();
            }
            if (phpjs.is_string(selectedFieldType) && selectedFieldType) {
                field_type_mandatory_fields.show();
                var context = e.attr('data-config') ? phpjs.json_decode(e.attr('data-config')) : {};
                context.field_type_title = field_type_title;

                field_type_set.html(WC.compileHandlebars(WC.hbsTmpl('field_types-setup-'+selectedFieldType.toLowerCase()), context));
                WC.utilities.applySortable('.field-type-config-item-container');
            }
            else {
                field_type_mandatory_fields.hide();
                field_type_set.html('');
            }
        }
    };

    that.fieldTypeAddConfigItem = function() {
        var item = $('.field-type-config-item:last-child', $(field_type_set_selector));
        if (item.length) {
            var container = item.parent();
            if (container.length) {
                var tagName = item[0].tagName.toLowerCase();
                var obj = $('<div>'+item.html()+'</div>');
                obj.find('input').attr('value', '');
                container.append('<'+tagName+' class="field-type-config-item">'+obj.html()+'</'+tagName+'>');
            }
        }
    };

    that.fieldTypeRemoveConfigItem = function(e) {
        e = $(e).parent();
        if (e.length) {
            e = e.parent();
            if (e.length) {
                e = e.parent();
                if (e.length) {
                    e = e.parent();
                    if (e.length && e.hasClass('field-type-config-item')) {
                        var items = $('.field-type-config-item');
                        if (items.length > 1) {
                            e.remove();
                        }
                    }
                }
            }
        }
    };

    that.productAttrsList = function() {
        var e = $('#product_attrs_set');
        if (e.length) {
            WC.httpClient({
                url: WC.constants.PRODUCT_ATTRIBUTE_LIST,
                success: function(data) {
                    data = data && data.data && (phpjs.is_object(data) || phpjs.is_array(data.data)) ? data.data : {};
                    e.html(WC.compileHandlebars(WC.hbsTmpl('product_attrs'), {product_attrs_list: data}));
                    WC.utilities.applySortable('.product_attrs_list');
                }
            });
        }
    };

    var defaultProductTypeAttrsFieldsetContent = null;

    that.productTypeAttrsFieldset = function(modalId) {
        var product_type_attrs_set = $('#product_type_attrs_set');
        if (product_type_attrs_set.length) {
            if (!defaultProductTypeAttrsFieldsetContent) {
                defaultProductTypeAttrsFieldsetContent = product_type_attrs_set.html();
            }
            var dataType = product_type_attrs_set.attr('data-type');
            if (dataType) {dataType = phpjs.json_decode(dataType);}
            var selectedValue = WC.formUtil.getVal(modalId, 'product_type');
            if (phpjs.sizeof(selectedValue) && selectedValue.id && (!dataType || dataType.id!==selectedValue.id)) {
                WC.httpClient({
                    url: WC.constants.COMMON_PRODUCT_TYPE_ATTRS_LIST.replace('{parent_id}', selectedValue.id).replace('{id}', selectedValue.id),
                    success: function(data){
                        var html = [];
                        if (data.data && phpjs.is_array(data.data)) {
                            data = data.data;
                            for (var i in data) {
                                html.push('<div class="attr_field">'+WC.compileHandlebars(WC.hbsTmpl(data[i].field_type+'-form'), data[i])+'</div>');
                            }
                        }
                        product_type_attrs_set.html(html.join(''));
                    }
                });
            }
            else if (defaultProductTypeAttrsFieldsetContent !== null) {
                product_type_attrs_set.html(defaultProductTypeAttrsFieldsetContent);
            }
        }
    };

    var storeChildrenContent = null, fileChildrenContent = null, categoryChildrenContent = null;

    that.productStores = function(action, ele, parentSelector, childrenSelector) {
        var childrenElements = $(childrenSelector);
        var numChildren = childrenElements.length;
        if (storeChildrenContent === null && numChildren) {
            var e = $(childrenElements[0]);
            storeChildrenContent = $('<div class="'+e.attr('class')+'">'+e.html()+'</div>');
            storeChildrenContent.find('input').attr('value', '');
            storeChildrenContent = '<div class="'+e.attr('class')+'">'+storeChildrenContent.html()+'</div>';
        }
        if (action ==='add' && storeChildrenContent) {
            $(parentSelector).append(storeChildrenContent);
        }
        else if (action === 'remove' && numChildren > 1) {
            $(ele).parent().parent().remove();
        }
    };

    that.productCategories = function(action, ele, parentSelector, childrenSelector) {
        var childrenElements = $(childrenSelector);
        var numChildren = childrenElements.length;
        if (categoryChildrenContent === null && numChildren) {
            var e = $(childrenElements[0]);
            categoryChildrenContent = $('<div class="'+e.attr('class')+'">'+e.html()+'</div>');
            categoryChildrenContent.find('input').attr('value', '');
            categoryChildrenContent = '<div class="'+e.attr('class')+'">'+categoryChildrenContent.html()+'</div>';
        }
        if (action ==='add' && categoryChildrenContent) {
            $(parentSelector).append(categoryChildrenContent);
        }
        else if (action === 'remove' && numChildren > 1) {
            $(ele).parent().parent().remove();
        }
    };

    that.productFiles = function(action, ele, parentSelector, childrenSelector) {
        var childrenElements = $(childrenSelector);
        var numChildren = childrenElements.length;
        if (fileChildrenContent === null && numChildren) {
            var e = $(childrenElements[0]);
            fileChildrenContent = $('<div class="'+e.attr('class')+'">'+e.html()+'</div>');
            fileChildrenContent.find('input').attr('value', '');
            fileChildrenContent.find('.img_preview').html('');
            fileChildrenContent.find('.doc_link').html('');
            fileChildrenContent = '<div class="'+e.attr('class')+'">'+fileChildrenContent.html()+'</div>';
        }
        if (action ==='add' && fileChildrenContent) {
            $(parentSelector).append(fileChildrenContent);
            WC.utilities.applyLightBox();
        }
        else if (action === 'remove' && numChildren > 1) {
            $(ele).parent().parent().parent().remove();
        }
    };

    return that;
}();