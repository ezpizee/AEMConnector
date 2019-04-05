WC.cardType = function() {
    var that = {};
    var types = {
        amex:               "34,37",
        chinaunionpay:         "62,88",
        dinersclubcarteblanche:   "300-305",
        dinersclubcarteinternational:      "300-305,309,36,38-39",
        dinersclubuscanada:      "54,55",
        discover:           "6011,622126-622925,644-649,65",
        jcb:                "3528-3589",
        laser:              "6304,6706,6771,6709",
        maestro:            "5018,5020,5038,5612,5893,6304,6759,6761,6762,6763,0604,6390",
        dankort:            "5019",
        mastercard:         "300-305",
        visa:               "4",
        visaelectron:       "4026,417500,4405,4508,4844,4913,4917"
    };
    var names = {
        amex: "American Express",
        chinaunionpay:"China UnionPay",
        dinersclubcarteblanche:"Diners ClubCarte Blanche",
        dinersclubcarteinternational:"Diners Club International",
        dinersclubuscanada:"Diners Club US & Canada",
        discover:"Discover Card",
        jcb:"JCB",
        laser:"Laser",
        maestro:"Maestro",
        dankort:"Dankort",
        mastercard:"MasterCard",
        visa:"Visa",
        visaelectron:"Diners ClubCarte Blanche",
    };
    for (var k in types) {types[k]=types[k].split(",");}
    that.detect = function(n) {
        if (phpjs.is_numeric(n)) {
            for(var i in types) {
                for (var j in types[i]) {
                    var parts = types[i][j].split("-");
                    if (parts.length === 1) {
                        var n1 = parseInt(parts[0]);
                        var l1 = parts[0].length;
                        if (n.length >= l1) {
                            var n3 = parseInt(phpjs.substr(n, 0, l1));
                            if (n3 === n1) {
                                return i;
                            }
                        }
                    }
                    else if (parts.length === 2) {
                        var n1 = parseInt(parts[0]), n2 = parseInt(parts[1]);
                        var l1 = parts[0].length, l2 = parts[1].length;
                        if (n.length >= l1 && n.length <= l2) {
                            var n3 = parseInt(phpjs.substr(n, 0, l1));
                            if (n3 >= n1 && n3 <= n2) {
                                console.log(names[i]);
                                return i;
                            }
                        }
                    }
                }
            }
        }
    };
    return that;
}();