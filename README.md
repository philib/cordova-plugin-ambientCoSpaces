## Cordova / Phonegap plugin for AmbientCospaces Project at HTWG Konstanz
### Installation
**Installation from local directory:**
1. open shell or cmd
2. navigate to root path of ionic application
3. type :  ``` cordova plugin add /path/to/cordova/plugin --nofetch ```

**Installtion from remote npm repository**
1. open shell or cmd line
2. navigate to root path of ionic application
3. type: ```cordova plugin add cordova-plugin-ambientcospaces --save```


### Functions

**startForegroundPositioning(JSONObject user) :**

Starts the positioning in foreground mode (non sticky). As long as the app is running (currently in the foreground or minimized) the positioning algorithm detects the current position and posts this information to the backend.
On start a userobject needs to be passed as parameter to be able to update the users position.

**startBackgroundPositioning(JSONObject user) :**

Starts the positioning in background mode (sticky). The positioning serivce is still running or will be restarted by the system if app is not running, gets killed by user or the system or the device is rebooting
On start a userobject needs to be passed as parameter to be able to update the users position

**stopPositioning() :**

Stops the positioning service whether it is running in foreground- or background mode.

### Usage

```TypeScript
import ...;

declare let cordova;

@Injectable()
export class YourClass{
  private currentUser;
  constructor(...) {
    ...
  }

  startForeground(){
    cordova.plugins.AmbientCoSpaces.startForegroundPositioning(this.currentUser);
  }

  stopPositioning(){
    cordova.plugins.AmbientCoSpaces.stopPositioning();
  }
}
```