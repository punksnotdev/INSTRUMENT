MIDIDevice {

    var <controllers;
    var <groups;
    var device;
    var <>name;
    var <slug;

    *new {|device|
        ^super.new.init(device);
    }

    init {|device_|

        device = device_;
        name = device.device;
        slug = name.replace(" ","_").toLower.asSymbol;
        groups = ();

		controllers = ();

	}

    addControllerGroup {|type, name|
        groups[name] = ControllerGroup(type,name,this);
    }

    removeControllerGroup {|name|
        if( groups[name].notNil, {
            groups.removeAt(name);
        });
    }



	set {|source, value|

		var key = source.key;
		var range = controllers[source.key].range;
        var normalizedValue;

        normalizedValue = (value / 127).asFloat;

        ["set:",key,normalizedValue].postln;

        
	}

}
