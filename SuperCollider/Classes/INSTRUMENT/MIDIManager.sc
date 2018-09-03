MIDIManager {

    var <devices;

    *new {
        ^super.new.init();
    }

    init {
        devices = ();
    }


    addDevice{|device|
        var key = device.device.replace(" ","_").toLower();
        key = key.asSymbol;

        devices[key] = MIDIDevice(device);
        ^devices[key]
    }

    removeDevice{|deviceName|
        devices.removeAt( deviceName );
    }

}
