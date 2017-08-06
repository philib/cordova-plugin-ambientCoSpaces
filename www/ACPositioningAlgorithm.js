var exec = require('cordova/exec');

exports.startPositioning = function(arg0, success, error) {
    exec(success, error, "ACPositioningAlgorithm", "startPositioning", [arg0]);
};

exports.stopPositioning = function(arg0, success, error) {
    exec(success, error, "ACPositioningAlgorithm", "stopPositioning", [arg0]);
};
