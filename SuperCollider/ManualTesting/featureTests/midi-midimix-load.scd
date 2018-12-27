(

	var midi;
	var midiDevicesNames;
	//
	// s.boot;
	//
	// s.doWhenBooted({

	Tdef(\start, { 1.do {

		// i=INSTRUMENT();

		5.wait;

		"setup midi".postln;

		i.midi=true;
		midi = i.controllerManager.midi;


		3.wait;

		// midiDevicesNames = ["MIDI Mix"];
		// midiDevicesNames = ["MIDI Mix","USB Axiom 25 MIDI 1"];
		// midiDevicesNames = ["MIDI Mix", "USB Axiom 25 MIDI 1"];
		midiDevicesNames = ["MIDI Mix"];

		midi.addDevices( midiDevicesNames );





		m = i.midiControllers;


		"/home/mukkekunst/Musica/SuperCollider/INSTRUMENT/SuperCollider/ControllerMaps/midimix.map.scd".load;


		"/home/mukkekunst/Musica/SuperCollider/INSTRUMENT/SuperCollider/ControllerMaps/midimix.setup.scd".load;


		m.inputs=List.new;


		m.sortedKeysValuesDo({|k,v,i|
			if(i<8) {
				m.inputs.add(m[k].fader);
			}
		});

		// "/home/mukkekunst/Musica/SuperCollider/INSTRUMENT/SuperCollider/ControllerMaps/axiom.setup.scd".load;






		} }).play;

	// });

)
