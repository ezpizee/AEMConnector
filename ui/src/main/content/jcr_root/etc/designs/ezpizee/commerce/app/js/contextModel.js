WC.contextModel = function(data) {
    this.data = {};
    if (phpjs.is_object(data)) {
        this.data = data;
    }
    else if (phpjs.is_string(data) && WC.utilities.isJSON(data)) {
        this.data = phpjs.json_decode(data);
    }
    else {
        console.error('Argument data allowed to be either object or JSON string.');
    }
    this.set = function(k, v) {if(!this.exists(k)){this.data[k] = v;}};
    this.get = function(k, v) {return this.data[k]||v;};
    this.exists = function(k) {return this.data[k] !== undefined;};
    this.has = function(k) {return this.data[k] !== undefined;};
    this.remove = function(k) {if (this.exists(k)) {delete this.data[k];}};
    this.update = function(k, v) {if(this.exists(k)){this.data[k] = v;}};
    this.length = function() {return phpjs.sizeof(this.data);};
    this.toString = function() {return phpjs.json_encode(this.data);};
    this.jsonSerialize = function() {return this.data;};
};