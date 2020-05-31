MIDIManager {

    var <devices;

    var >controllerManager;

    var mainMIDIOutput;

    *new {|controllerManager_|
        ^super.new.init(controllerManager_);
    }

    init {|controllerManager_|
        controllerManager = controllerManager_;
        devices = ();
    }

    addDevices{|midiDevicesNames,specs|

        MIDIClient.sources.collect({|device|
            midiDevicesNames.collect({|midiDeviceName|
                var midiDevice;
                if( device.name == midiDeviceName, {
                    midiDevice = device;
                }, {
                    if( device.device == midiDeviceName, {
                        midiDevice = device;
                    });
                });
                if( midiDevice.notNil, {
                    if( specs.notNil, {
                        [
                            midiDeviceName.asString.toLower.replace(" ","_").asSymbol,
                            specs[ midiDeviceName.asString.toLower.replace(" ","_").asSymbol ]
                        ].postln;
                        
                        this.addDevice( midiDevice, specs[ midiDeviceName.asString.toLower.replace(" ","_").asSymbol ] );
                    }, {
                        this.addDevice( midiDevice );
                    });

                });
            });
        });

        ^devices

    }

    addDevice{|device,spec|

        var key = device.device.replace(" ","_").toLower();

        key = key.asSymbol;

        devices[key] = MIDIDevice(this,device,spec);

        ^devices[key]

    }

    removeDevice{|deviceName|
        devices.removeAt( deviceName );
    }


    set {|key,param1,param2|

        ^controllerManager.set(key,param1,param2);

    }


    setupMIDIOut {

        "MM setupMIDIOut".warn;

        if( mainMIDIOutput.notNil, {


            Tdef(\MidiClock).set(\bpm, TempoClock.default.tempo*120);

            Tdef(\MidiClock, {
            	var period, tick;


            	inf.do{

                    period = 1/2;//(60/(TempoClock.default.tempo*120));
                    tick = period/24;

            		tick.wait;
            	}

            });

            Tdef(\MidiClock).play;

        });

    }

}
