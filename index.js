'use strict';

var RNPayment = require('react-native').NativeModules.RNPayment;
module.exports = {
  registerApp: function(appId) {
    return RNPayment ? RNPayment.registerApp(appId) : false;
  },

  alipay: function(orderInfo) {
    return RNPayment ? RNPayment.alipay(orderInfo) : false;
  },

  wxpay: function(orderInfo) {
    return RNPayment ? RNPayment.wxpay(orderInfo) : false;
  }

};
