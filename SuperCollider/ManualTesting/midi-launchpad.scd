(

	s.options.memSize=2048*1024;
	s.options.maxNodes=128*1024;

	s.boot;
 // MIDIClient.restart;
 	MIDIClient.init;

	s.doWhenBooted({

		e=();

		e.midiDevicesNames;
		e.specs;
		e.midi;
		e.modeMatrix;
		e.spec;


		e.modeMatrix = ModeMatrix.new();
		e.specs = IdentityDictionary.new;


		"/home/furenku/Music/SuperCollider/INSTRUMENT/SuperCollider/ControllerMaps/launchpad.scd".load;
		e.specs[ o[\name].asSymbol ] = o;
		Tdef(\start, { 1.do {

			i=INSTRUMENT();
			// i.play;

			i.tempo=333;


			// 1.wait;
			MIDIClient.init(verbose:false);

			2.wait;

			i.startMidi();

			e.midi = i.controllerManager.midi;

			// 0.wait;

			// midiDevicesNames = ["Launchpad", "USB Axiom 25 MIDI 1"];
			e.midiDevicesNames = ["Launchpad"];

			e.midi.addDevices( e.midiDevicesNames, e.specs );

			1.wait;


			i.midi.launchpad.addControllerGroup(\note,\buttons);
			i.midi.launchpad.groups.buttons.addController(\buttons);



			1.wait;

			i.map(
				i.midi.launchpad.groups.buttons.controllers.buttons,
				e.modeMatrix,
				\note
			);

			i.piano=INSTRUMENT(\distNote1);

			// i.map(
			// 	e.modeMatrix,
			// 	// i.midi.launchpad.groups.buttons.controllers.buttons,
			// 	i.midi.launchpad,
			// 	\note
			// );

			// i.map(
			// 	// e.modeMatrix,
			// 	i.midi.launchpad.groups.buttons.controllers.buttons,
			// 	i.piano,
			// 	\note
			// );

	 		e.modeMatrix.midiTarget = i.midi.launchpad;



			i.data.loopers=Array.fill(8,{|index|
				var chan = (index/4).floor.asInteger;

				(
						instrument: Looper(chan),
						amp: 		1,
						rate: 		4,
						fx: 		0,
				)

			});

			CmdPeriod.add({MIDIClient.disposeClient;});

			"Loopers: Loaded".postln;


			// 0.wait;

			72.do{|j|
				i.midi.launchpad.send(j,90+24.rand+(j%32));
			};
			Array.series(8,8,9).collect{|num|
				i.midi.launchpad.send(num,3);
				// 0.25.wait;
			};

			i.synths = i.loadSynths(Platform.userExtensionDir++'/INSTRUMENT/INSTRUMENT/Sounds/SynthDefs/*');

			"Synths Loaded".postln;

			(0.5).wait;

			"ModeMatrix: Lauchpad connected".postln;




			i.data.synths.parameter_ranges=();
			i.data.synths.parameter_ranges.amp=[0,1];
			i.data.synths.parameter_ranges.wet=[0,1];
			i.data.synths.parameter_ranges.gain=[1,100];
			i.data.synths.parameter_ranges.cutoff=[50,12000];


			i.data.lastChanThatUsedFX = IdentityDictionary.new;


			i.play;

			"INSTRUMENT: play".postln;

		}}).play;




	});

)
