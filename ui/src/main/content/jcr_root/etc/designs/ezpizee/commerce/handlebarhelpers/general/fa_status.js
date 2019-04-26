Handlebars.registerHelper('fa_status', function() {
    var $buffer = arguments[0]||'';
    if ($buffer) {
        if (phpjs.is_numeric($buffer)) {
            $buffer = parseInt($buffer);
            $buffer = '<i class="fa fa-'+($buffer>0?'check-circle':'circle')+'"></i>';
        }
        else {
            $buffer = '<i class="fa fa-'+($buffer==='yes'?'check-circle':'circle')+'"></i>';
        }
    }
    return $buffer;
});