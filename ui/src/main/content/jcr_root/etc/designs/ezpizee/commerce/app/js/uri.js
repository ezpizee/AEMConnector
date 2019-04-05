WC.uri = function() {
    var h = window.location.toString().split("?");
    this.host = location.host;
    this.hostname = location.hostname;
    this.schema = location.protocol;
    this.path = h[0].replace(this.schema+'//'+this.host, '');
    this.queryString = h.length === 2 ? h[1] : '';
    this.query = {};
    this.fragment = location.hash;
    if (this.queryString) {
        this.path = this.path.replace('?'+this.queryString, '');
        var parts = this.queryString.split("&");
        for(var i in parts) {
            var parts2 = parts[i].split("=");
            if (parts2.length === 2) {
                this.query[parts2[0]] = parts2[1];
            }
        }
    }
    if (this.fragment) {
        this.fragment = this.fragment.replace("#!/", "/");
    }
};