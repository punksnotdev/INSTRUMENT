MIDIDevice {

    var <controllers;
    var <output;
    var <inputMap;
    var <outputMap;
    var <groups;
    var device;
    var spec;
    var <>name;
    var <id;
    var <slug;
    var >midi;
    var <protocol;

    *new {|midiManager,device, spec_|
        ^super.new.init(midiManager,device, spec_);
    }

    init {|midiManager,device_, spec_|

        midi = midiManager;
        device = device_;
        protocol = "midi";
        name = device.device;
        id = device.uid;

        spec=spec_;

        slug = name.replace(" ","_").toLower.asSymbol;
        groups = ();

		controllers = ();

        if( spec_.notNil, {

            // if( spec.outputs.isInteger, {
            var port;

            // MIDIClient.destinations.collect({|destination, index|
            //
            //     if(spec.name == destination.device, {
            //         port = index;
            //     });
            //
            // });
            if( spec.isKindOf(Event), {

                inputMap = spec.inputMap;
                outputMap = spec.outputMap;
            });

            outputMap.collect({|outputMapping|
                outputMapping.type=spec.outputType;
            });

            // if( port.notNil, {


                output = MIDIOut( 0 );

                // output.connect( port );

            // });

        });



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

    send {|key,value|

        if( outputMap.notNil, {
            if( outputMap[key].notNil, {

                switch( outputMap[key].type,
                    \note, {
                        output.noteOn(0,outputMap[key].ctlNum,value)
                    }
                );

            });
        });

    }

	set {|source, param1, param2|

        ^midi.set(source,param1,param2);

	}


}
