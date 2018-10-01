(


	var midi;
	var midiDevicesNames;
	var specs;

	specs=IdentityDictionary.new;


	"/home/mukkekunst/Musica/SuperCollider/INSTRUMENT/SuperCollider/ControllerMaps/launchpad.scd".load;



	specs[ o[\name].asSymbol ] = o;
	[o[\name].asSymbol,specs[ o[\name].asSymbol ]].postln;


	s.boot;

	s.doWhenBooted({

	Tdef(\start, { 1.do {

		i=INSTRUMENT();

		i.midi=true;
		midi = i.controllerManager.midi;


		3.wait;

		midiDevicesNames = ["Launchpad"];

		MIDIClient.sources.collect({|device|
			midiDevicesNames.collect({|midiDeviceName|
				if( device.device == midiDeviceName, {
					var midiDevice = device;
					MIDIIn.connect(0, midiDevice);
					midi.addDevice( midiDevice, specs[ midiDeviceName.asSymbol ] );
				});
			});
		});

		1.wait;

		72.do{|j|
			i.midi.launchpad.send(j,90+(j%32));
		};

	}}).play;


});

)
