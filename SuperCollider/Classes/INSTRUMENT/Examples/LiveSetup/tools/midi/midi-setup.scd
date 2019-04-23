(

	var midi;
	var inMidiDevicesNames;
	var outMidiDevicesNames;
	var specs = IdentityDictionary.new;

	e = ();

	e.modeMatrix = ModeMatrix.new();


	(thisProcess.nowExecutingPath.dirname ++ "/../../../midi/controllers/midimix/midimix.map.scd").load;


	specs[ b[\name].asSymbol ] = b;

	(thisProcess.nowExecutingPath.dirname ++ "/../../../midi/controllers/launchpad/launchpad.map.scd").load;
	specs[ o[\name].asSymbol ] = o;

	Tdef(\MIDI, { 1.do {

		//i=INSTRUMENT();

		MIDIClient.init(verbose:false);

		3.wait;
		i.startMidi();

		midi = i.controllerManager.midi;

		3.wait;

		inMidiDevicesNames = (thisProcess.nowExecutingPath.dirname ++ "/../../../midi/devices-in.scd").load;

		outMidiDevicesNames = (thisProcess.nowExecutingPath.dirname ++ "/../../../midi/devices-out.scd").load;

		midi.addDevices( inMidiDevicesNames, specs );


		1.wait;

		m=();


		(thisProcess.nowExecutingPath.dirname ++ "/../../../midi/controllers/mpk_mk2/mpk_mk2.namespace.scd").load;

		(thisProcess.nowExecutingPath.dirname ++ "/../../../midi/controllers/midimix/midimix.namespace.scd").load;

		i.midi.launchpad.addControllerGroup(\note,\buttons);
		i.midi.launchpad.groups.buttons.addController(\buttons);

		// (thisProcess.nowExecutingPath.dirname ++ "/../../../midi/controllers/launchpad/launchpad.namespace.scd").load;

		1.wait;




		i.map(
			i.midi.launchpad.groups.buttons.controllers.buttons,
			e.modeMatrix,
			\note
		);

		e.modeMatrix.midiTarget = i.midi.launchpad;


		i.data.modeMatrix = e.modeMatrix;


		CmdPeriod.add({MIDIClient.disposeClient;});


		72.do{|j|
			i.midi.launchpad.send(j,90+24.rand+(j%32));
		};
		Array.series(8,8,9).collect{|num|
			i.midi.launchpad.send(num,3);
			// 0.25.wait;
		};

		"Launchpad: Loaded".postln;






	}});

	Tdef(\MIDI).play;


)
