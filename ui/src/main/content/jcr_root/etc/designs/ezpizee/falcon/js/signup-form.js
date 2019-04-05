function signUpForm() {
    var form = $('#signup-form'), steps, goTo, plan = "", selectPlan, plans, debug = false;
    if (form.length) {
        var uri = new WC.uri();
        steps = form.find('[data-step]');
        goTo = form.find('[data-goto]');
        selectPlan = form.find('[data-select]');
        plans = form.find('[data-plan]');
        if (steps.length) {
            $(steps[0]).removeClass('hide');
            if (goTo.length) {
                goTo.click(function(){
                    var e = $(this), v = e.attr('data-goto'), proceed = true;
                    form.find('[data-step="'+(v-1)+'"] [data-validation]').each(function(){
                        var e = $(this), tagName = e[0].tagName.toLowerCase();
                        if (proceed) {
                            if (tagName === 'input') {
                                if (e.attr('type') === 'checkbox' || e.attr('type') === 'radio') {
                                    if (!e.is(':selected') && !e.is(':checked')) {
                                        proceed = false;
                                    }
                                }
                                else if (!e.val()) {
                                    proceed = false;
                                }
                            }
                            else if (tagName === 'select') {
                                if (e.find('option:selected').length) {
                                    e.find('option:selected').each(function(){
                                        if (!$(this).attr('value')) {
                                            proceed = false;
                                        }
                                    });
                                }
                                else {
                                    proceed = false;
                                }
                            }
                            if (proceed && e.hasClass('error')) {
                                if (e.next('.form-error').length) {
                                    e.next('.form-error').addClass('show');
                                }
                                proceed = false;
                            }
                        }
                    });
                    if (proceed || debug) {
                        hide(steps);
                        show(form.find('[data-step="'+v+'"]'));
                    }
                    else {
                        WC.renderAlert('error', 'Please fill in, check, and/or select every field', 'signup-form-msg');
                    }
                });
            }
            if (selectPlan.length) {
                var selectedPlan = selectPlan.find('option:selected');
                if (!selectedPlan.length) {selectedPlan=selectPlan.find('option:first-child');}
                if (selectedPlan.length) {
                    plans.html(WC.compileHandlebars(WC.hbsTmpl('plan-'+selectedPlan.attr('value')), {}));
                }
                selectPlan.on('change', function(){plans.html(WC.compileHandlebars(WC.hbsTmpl('plan-'+this.value), {}));});
            }
            if (uri.query.step && form.find('[data-step="'+uri.query.step+'"]').length) {
                hide(steps);
                show(form.find('[data-step="'+uri.query.step+'"]'));
            }
            var billingEle = $('[name="wc_content_form[payment_method_id]"]');
            if (billingEle.length) {
                var billingEleChild = $('[data-child-of]');
                billingEle.click(function(e){
                    if (WC.hasHbsTmpl('child-of-'+e.target.value)) {
                        var html = WC.hbsTmpl('child-of-'+e.target.value);
                        if (html) {
                            billingEleChild.html(WC.compileHandlebars(html, {}));
                        }
                        else {
                            billingEleChild.html('');
                        }
                    }
                });
            }
        }
    }

    function hide(e) {e.addClass('hide');}
    function show(e) {e.removeClass('hide');}
    function isTrial() {return plan === "trial";}
}