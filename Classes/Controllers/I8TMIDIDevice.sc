MIDIDevice {

    var device;

    var <controllers;
    var <controllerTargets;
    var <out; // shorthand for 'controllerTargets'

    var <deviceInput;
    var <>name;
    var <id;
    var <slug;
    var >midi;
    var <protocol;
    var <spec;

    *new {|midiManager,device, spec|
        ^super.new.init(midiManager,device, spec);
    }

    init {|midiManager,device_, spec_|

        midi = midiManager;

        protocol = "midi";

        device = device_;

        name = device.device;

        id = device.uid;


        slug = name.replace(" ","_").toLower.asSymbol;

		controllers = ();
		controllerTargets = ();

        out = controllerTargets;

        if( spec_.notNil, {

            var port;

            spec = I8TControllerSpec.new(spec_);

            this.setupControllers();

        });

        this.setupMIDIOut();


	}



    sendNote {|key,value|
        this.send(\note,key,value);
    }

    sendControl {|key,value|
        this.send(\cc,key,value);
    }

    send {|target,type,key,value|
        ['got send msg', target, type,key,value].postln;
        ['spec is', target.name].postln;
        switch( type,
            \note, {
                // deviceInput.noteOn(0,key.asInteger,value.asInteger.min(127))
            },
            \cc, {
                if( spec.outputMap.notNil ) {
                    if( spec.outputMap[type][key].notNil) {
                        // deviceInput.control(0,spec.outputMap.cc[key],value.min(127))
                    };
                };
            },
        );

    }

	set {|source, param1, param2|

        ^midi.set(source,param1,param2);

	}


    setupMIDIOut {

        var thisDevice = MIDIClient.destinations.detect({|d|
            d.device == device.device
        });

        if( thisDevice.notNil ) {
            deviceInput = MIDIOut.newByName( thisDevice.device, thisDevice.name );
        };

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


        spec.inputs.keysValuesDo({|groupKey,group|
            switch(group.type,
                \note, {
                    controllerTargets[groupKey]=(
                        MIDIControllerTarget.new(
                            this,
                            groupKey,
                            id,
                            group.type,
                            nil,
                            group.channel
                        )
                    );

                    controllerTargets[groupKey].addCallback(\midiDevice,{
                        arg ...args;
                        this.send(args[0],args[1],args[2],args[3]);
                        ["target is", args[0]].postln
                    });
                },
                \cc, {
                    if( group.controllers.notNil, {
                        if( controllerTargets[groupKey].isNil) {
                            controllerTargets[groupKey] = ();
                        };

                        group.controllers.collect({|v,k|
                            controllerTargets[groupKey][k] = MIDIControllerTarget.new(
                                this,
                                groupKey,
                                id,
                                group.type,
                                v,
                                group.channel
                            );
                        });

                    }, {

                        controllerTargets[groupKey] = MIDIController.new(
                            this,
                            id,
                            groupKey,
                            group.type,
                            nil,
                            group.channel
                        );

                    });
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
