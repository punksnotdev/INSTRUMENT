(

	var midiDevicesNames;
	var specs;
	var midi;
	var modeMatrix;


	modeMatrix = ModeMatrix.new();
	specs = IdentityDictionary.new;


	"/home/mukkekunst/Musica/SuperCollider/INSTRUMENT/SuperCollider/ControllerMaps/launchpad.scd".load;
	specs[ o[\name].asSymbol ] = o;


	s.boot;

	s.doWhenBooted({

	Tdef(\start, { 1.do {

		i=INSTRUMENT();

		3.wait;

		i.midi=true;

		midi = i.controllerManager.midi;

		3.wait;

		// midiDevicesNames = ["Launchpad", "USB Axiom 25 MIDI 1"];
		midiDevicesNames = ["Launchpad"];

		midi.addDevices( midiDevicesNames, specs );

		1.wait;


		i.midi.launchpad.addControllerGroup(\note,\buttons);
		i.midi.launchpad.groups.buttons.addController(\buttons);

		// i.midi.usb_axiom_25.addControllerGroup(\note,\piano);
		// i.midi.usb_axiom_25.groups.piano.addController(\piano);



		72.do{|j|
			i.midi.launchpad.send(j,90+24.rand+(j%32));
		};
		Array.series(8,8,9).collect{|num|
			i.midi.launchpad.send(num,3);
		};



		i.map(
			i.midi.launchpad.groups.buttons.controllers.buttons,
			modeMatrix,
			\note
		);

		modeMatrix.midiTarget = i.midi.launchpad;

	}}).play;


});

)
