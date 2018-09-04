MIDIManager {

    var <devices;

    var >controllerManager;

    *new {|controllerManager_|
        ^super.new.init(controllerManager_);
    }

    init {|controllerManager_|
        controllerManager = controllerManager_;
        devices = ();
    }


    addDevice{|device|
        var key = device.device.replace(" ","_").toLower();
        key = key.asSymbol;

        devices[key] = MIDIDevice(this,device);
        ^devices[key]
    }

    removeDevice{|deviceName|
        devices.removeAt( deviceName );
    }


    set {|key,normalizedValue|

        ^controllerManager.set(key,normalizedValue);

    }

}
