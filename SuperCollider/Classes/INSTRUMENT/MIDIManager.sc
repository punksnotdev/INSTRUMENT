MIDIManager {

    var <devices;

    var >controllerManager;

    *new {|controllerManager_|
        ^super.new.init(controllerManager_);
    }

    init {|controllerManager_|
        controllerManager = controllerManager_;
        devices = ();
        this.setupMIDIOut();
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
                        this.addDevice( midiDevice, specs[ midiDeviceName.asSymbol ] );
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

        var midiOut=MIDIOut(0);

        Tdef(\MidiClock).set(\bpm, TempoClock.default.tempo*120);

        Tdef(\MidiClock, { |e|
        	var period, tick;

        	midiOut.start;

        	inf.do{
        		period = (60/e.bpm);
        		tick = period/24;
        		midiOut.midiClock;
        		tick.wait;
        	}

        });

        Tdef(\MidiClock).play;

    }

}
