MIDIDevice {

    var <controllers;
    var <output;
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

            this.setupControllers();

            // outputMap.collect({|outputMapping|
            //     outputMapping.type=spec.outputType;
            // });

            // if( port.notNil, {


            output = MIDIOut( 0 );

                // output.connect( port );

            // });

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


    setupControllers {

        spec.outputs.keysValuesDo({|groupKey,group|
            switch(group.type,
                \note, {
                    controllers[groupKey]=(
                        MIDIController.new(
                            this,
                            id,
                            group.type,
                            nil,
                            group.channel
                        )
                    );
                },
                \cc, {
                    if( group.controllers.notNil) {
                        if( controllers[groupKey].isNil) {
                            controllers[groupKey] = ();
                        };

                        group.controllers.collect({|v,k|
                            controllers[groupKey][k] = MIDIController.new(
                                this,
                                id,
                                group.type,
                                v,
                                group.channel
                            );
                        });

                    };
                }
            );
        });

    }



    doesNotUnderstand {

        arg selector ... args;

		var value = args[0];


        if (selector.isSetter==false) {
            ^controllers[selector]
		};


    }


}
