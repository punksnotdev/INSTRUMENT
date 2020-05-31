MIDIDevice {

    var <controllers;
    var <output;
    var <groups;
    var <>name;
    var <id;
    var <slug;
    var >midi;
    var <protocol;
    var <spec;

    *new {|midiManager,device, spec|
        ^super.new.init(midiManager,device, spec);
    }

    init {|midiManager,device, spec_|

        midi = midiManager;

        protocol = "midi";
        name = device.device;
        id = device.uid;


        slug = name.replace(" ","_").toLower.asSymbol;
        groups = ();

		controllers = ();

        if( spec_.notNil, {


            // if( spec.outputs.isInteger, {
            var port;

            spec = I8TControllerSpec.new(spec_);

            // MIDIClient.destinations.collect({|destination, index|
            //
            //     if(spec.name == destination.device, {
            //         port = index;
            //     });
            //
            // });



            // outputMap.collect({|outputMapping|
            //     outputMapping.type=spec.outputType;
            // });

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

        if( spec.outputMap.notNil, {
            if( spec.outputMap[key].notNil, {

                switch( spec.outputMap[key].type,
                    \note, {
                        output.noteOn(0,spec.outputMap[key],value)
                    }
                );

            });
        });

    }

	set {|source, param1, param2|

        ^midi.set(source,param1,param2);

	}


}
