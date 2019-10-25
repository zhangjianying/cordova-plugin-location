var exec = require('cordova/exec');

var TWLocation = {
  getLocation : function (onSuccess,onfail) {
    exec(onSuccess,onfail, 'TWLocation', 'getLocation', []);
  }
};

module.exports = TWLocation