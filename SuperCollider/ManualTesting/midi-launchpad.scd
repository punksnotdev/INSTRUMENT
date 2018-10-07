(


	var midi;
	var midiDevicesNames;
	var specs;

	specs=IdentityDictionary.new;


	"/home/mukkekunst/Musica/SuperCollider/INSTRUMENT/SuperCollider/ControllerMaps/launchpad.scd".load;
	specs[ o[\name].asSymbol ] = o;


	s.boot;

	s.doWhenBooted({

	Tdef(\start, { 1.do {

		i=INSTRUMENT();

		i.midi=true;
		midi = i.controllerManager.midi;


		3.wait;

		midiDevicesNames = ["Launchpad","USB Axiom 25 MIDI 1"];

		MIDIClient.sources.collect({|device|
			midiDevicesNames.collect({|midiDeviceName|
				if( device.name == midiDeviceName, {
					var midiDevice = device;
					midi.addDevice( midiDevice, specs[ midiDeviceName.asSymbol ] );
				}, {

					if( device.device == midiDeviceName, {
						var midiDevice = device;
						midi.addDevice( midiDevice, specs[ midiDeviceName.asSymbol ] );
					});

				});
			});
		});

		1.wait;

		// x = ModeMatrix.new();


		i.midi.launchpad.addControllerGroup(\note,\buttons);
		i.midi.launchpad.groups.buttons.addController(\buttons);
		i.midi.usb_axiom_25.addControllerGroup(\note,\piano);
		// i.midi.usb_axiom_25.groups.piano.addController(\piano,nil,0);
		i.midi.usb_axiom_25.groups.piano.addController(\piano);


		// x.addInput( i.midi.launchpad );
		// x.addOutput( i.midi.launchpad );


		// 72.do{|j|
		// 	i.midi.launchpad.send(j,90+24.rand+(j%32));
		// };

		// i.map( i.midi.launchpad.groups.buttons.controllers.buttons, nil );

	}}).play;


});

)
