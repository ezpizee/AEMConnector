WC.constants = function() {
    var that = {};
    var COMMERCE = '/commerce/';
    var API_URI_PFX = COMMERCE + 'api/';

    that.DELETE = API_URI_PFX + 'delete';

    that.COMMON_CONTRY_LIST = API_URI_PFX + 'data/common-country-list.json';
    that.COMMON_CURRENCY_LIST = API_URI_PFX + 'data/common-currency-list.json';
    that.COMMON_PAYMENT_SERVICE_LIST = API_URI_PFX + 'data/common-payment-service-list.json';
    that.COMMON_PRODUCT_TYPE_ATTRS_LIST = API_URI_PFX + 'data/product-type-attributes-list.json?parent_id={parent_id}';

    that.COUNTRY_LIST = COMMERCE + 'global-properties/countries.json';
    that.CURRENCY_LIST = COMMERCE + 'global-properties/currencies.json';
    that.TEMPERATURE_LIST = COMMERCE + 'global-properties/temperatures.json';
    that.LENGTH_LIST = COMMERCE + 'global-properties/length-measurement.json';
    that.WEIGHT_LIST = COMMERCE + 'global-properties/weight-measurement.json';
    that.ADDRESS_LIST = COMMERCE + 'global-properties/addresses.json';
    that.SUBSCRIPTION_PLAN_LIST = COMMERCE + 'global-properties/subscription-plans.json';

    that.FOLDER_LIST = COMMERCE + 'global-properties/assets/folders.json';
    that.FOLDER_CONTENT = COMMERCE + 'global-properties/assets/folders.html?tmpl=content';
    that.ASSETS_LIST = COMMERCE + 'global-properties/assets.json';
    that.ASSETS_CONTENT = COMMERCE + 'global-properties/assets.html?tmpl=content';
    that.ASSETS_BY_PARENT = COMMERCE + 'global-properties/assets/assets-by-parent.json';
    that.ASSET_DATA = COMMERCE + 'global-properties/assets/asset-data.json';

    that.STORE_LIST = COMMERCE + 'store-manager/stores.json';
    that.CATEGORY_LIST = COMMERCE + 'store-manager/categories.json';
    that.MANUFACTURER_LIST = COMMERCE + 'store-manager/manufacturers.json';
    that.TAX_LIST = COMMERCE + 'store-manager/taxes.json';
    that.PAYMENT_METHOD_LIST = COMMERCE + 'store-manager/payment-methods.json';
    that.SHIPPING_METHOD_LIST = COMMERCE + 'store-manager/shipping-methods.json';

    that.PRODUCT_ATTRIBUTE_LIST = COMMERCE + 'product-manager/attributes.json';
    that.PRODUCT_TYPE_LIST = COMMERCE + 'product-manager/types.json';
    that.PRODUCT_ATTRIBUTE_VALUE_LIST = COMMERCE + 'product-manager/attribute-values.json';
    that.PRODUCT_LIST = COMMERCE + 'product-manager/products.json';

    that.DISCOUNT_LIST = COMMERCE + 'offer-manager/discounts.json';
    that.BUNDLE_LIST = COMMERCE + 'offer-manager/bundles.json';
    that.CROSS_SELL_LIST = COMMERCE + 'offer-manager/cross-sells.json';
    that.UP_SELL_LIST = COMMERCE + 'offer-manager/up-sells.json';

    that.CART_ITEM_LIST = COMMERCE + 'cart/items.json';

    that.HBS_PAYMENT_SERVICE = {
        "PAYPAL": "payment-service-paypal",
        "STRIPE": "payment-service-stripe"
    };
    that.HBS_MODAL = 'modal';
    that.HBS_RADIO_BUTTON_LIST = 'radio-button-list';
    that.HBS_ALERT_MESSAGE = 'alert-message';

    that.ATTR_DATA_CONFIG = 'data-config';
    that.ATTR_DATA_OBJECT = 'data-object';
    that.ATTR_DATA_HOOK_PFX = 'data-hook';

    return that;
}();