var exec = require('cordova/exec');

exports.startBackgroundPositioning = function(arg0, success, error) {
    exec(success, error, "AmbientCoSpaces", "startBackgroundPositioning", [arg0]);
};

exports.startForegroundPositioning = function(arg0, success, error) {
    exec(success, error, "AmbientCoSpaces", "startForegroundPositioning", [arg0]);
};

exports.stopPositioning = function(arg0, success, error) {
    exec(success, error, "AmbientCoSpaces", "stopPositioning", [arg0]);
};
