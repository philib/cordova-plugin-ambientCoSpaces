## Cordova / Phonegap plugin for AmbientCospaces Project at HTWG Konstanz

### Future Features

 * Starting and stopping a non sticky positioning algorithm background service

### Installation

* Inside your cordova project execute :

```
cordova plugin add /path/to/root/folder
```

### Usage

```
declare var cordova;
...

cordova.plugins.ACPositioningAlgorithm.startPositioning();
cordova.plugins.ACPositioningAlgorithm.stopPositioning();
```