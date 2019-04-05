WC.cookie = function() {
    var that = {};
    that.get = function(name) {var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');return v?fromCValue(v[2]):null;};
    /**
     * @param obj is javascript object of {name:"",value:"",expires:"(optional)",path:"(optional)",domain:"(optional)",secure:"(optional)"}
     */
    that.set = function(obj) {
        if (phpjs.is_object(obj)) {
            var name = obj.name||'';
            var value = toCValue(obj.value||'');
            if (name && value) {
                if (obj.days) {
                    var d = new Date;
                    d.setTime(d.getTime() + 24*60*60*1000*obj.days);
                    obj.expires = d.toGMTString();
                }
                document.cookie = name + "=" + value +
                    ((obj.expires) ? "; expires=" + obj.expires : "") +
                    ("; path=" + (obj.path||"/")) +
                    ((obj.domain) ? "; domain=" + obj.domain : "") +
                    ((obj.secure) ? "; secure" : "");
            }
        }
        else {
            console.error("obj has to be an object");
        }
    };
    that.delete = function(name) {that.set({name:name,value:'',days:-1});};
    function toCValue(v) {return v && (phpjs.is_array(v) || phpjs.is_object(v)) ? phpjs.base64_encode(phpjs.json_encode(v)) : v;}
    function fromCValue(v) {return v && WC.utilities.isBase64Encoded(v) ? phpjs.json_decode(phpjs.base64_decode(v)) : v;}
    return that;
}();