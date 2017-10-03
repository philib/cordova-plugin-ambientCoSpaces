var exec = require('cordova/exec');

exports.startBackgroundPositioning = function(user, url) {
    exec(null, null, "AmbientCoSpaces", "startBackgroundPositioning", [user, url]);
};

exports.startForegroundPositioning = function(user, url) {
    exec(null, null, "AmbientCoSpaces", "startForegroundPositioning", [user, url]);
};

exports.stopPositioning = function(arg0, success, error) {
    exec(success, error, "AmbientCoSpaces", "stopPositioning", [arg0]);
};

exports.startNotificationSubscriptionService = function(arg0, success, error) {
    exec(success, error, "AmbientCoSpaces", "startNotificationSubscriptionService", [arg0]);
};

exports.stopNotificationSubscriptionService = function(arg0, success, error) {
    exec(success, error, "AmbientCoSpaces", "stopNotificationSubscriptionService", [arg0]);
};
