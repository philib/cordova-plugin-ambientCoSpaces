var exec = require('cordova/exec');

exports.startPositioning = function(arg0, success, error) {
    exec(success, error, "AmbientCoSpaces", "startPositioning", [arg0]);
};

exports.stopPositioning = function(arg0, success, error) {
    exec(success, error, "AmbientCoSpaces", "stopPositioning", [arg0]);
};
