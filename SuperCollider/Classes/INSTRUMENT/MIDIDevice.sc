MIDIDevice {

    var <controllers;
    var <groups;
    var device;
    var <>name;
    var <slug;
    var >midi;
    var <protocol;
    *new {|midiManager,device|
        ^super.new.init(midiManager,device);
    }

    init {|midiManager,device_|
        midi = midiManager;
        device = device_;
        protocol = "midi";
        name = device.device;
        slug = name.replace(" ","_").toLower.asSymbol;
        groups = ();

		controllers = ();

	}

    addControllerGroup {|type, name|
        groups[name] = ControllerGroup(type,name,this);
        ^groups[name];
    }

    removeControllerGroup {|name|
        if( groups[name].notNil, {
            groups.removeAt(name);
        });
    }



	set {|source, value|
        var key = source.key;
		var normalizedValue;

        normalizedValue = (value / 127).asFloat;


        ^midi.set(source,normalizedValue);

	}


}
