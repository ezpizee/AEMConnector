WC.httpClient = function(o) {
    if (phpjs.is_object(o) && o.url) {
        var ezpzSpinner = null;
        if (!o.noSpinner) {
            ezpzSpinner = WC.spinner.start();
        }
        $.ajax({
            type: o.type||'GET',
            url: o.url,
            data: o.data||'',
            dataType: o.dataType||'json',
            beforeSend: function(xhr) {
                xhr.setRequestHeader('csrftoken', WC.params.get('csrftoken', ''));
                if (o.beforeSend) {
                    (o.beforeSend)(xhr);
                }
            },
            success: function(d){
                if(ezpzSpinner !== null) {
                    ezpzSpinner.stop();
                }
                if (o.success && phpjs.is_callable(o.success)) {
                    (o.success)(d);
                }
            },
            error: function(a,b,c){
                if(ezpzSpinner !== null) {
                    ezpzSpinner.stop();
                }
                if (o.error && phpjs.is_callable(o.error)) {
                    (o.error)(a,b,c);
                }
                else {
                    WC.renderAlert('error', 'Error. ' + b + '; '+ c);
                }
            }
        });
    }
};