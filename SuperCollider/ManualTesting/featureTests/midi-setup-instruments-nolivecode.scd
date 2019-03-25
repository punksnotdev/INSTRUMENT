(

	var midiDevice;
	var midiDeviceName = "MIDI Mix";
	s.boot;

	s.doWhenBooted({

		var synths;
		Tdef(\doTest, { 1.do{

			i=INSTRUMENT();

			"/home/furenku/Music/SuperCollider/INSTRUMENT/SuperCollider/ControllerMaps/midimix.scd".load;

			i.midi=true;

			2.wait;

			synths = [
				SynthPlayer(\bass0,\tranceBazz),
				SynthPlayer(\bass1,\tranceBazz2),
				SynthPlayer(\bass2,\tranceBazz3),
				SynthPlayer(\bass3,\tranceBazz2),
				SynthPlayer(\bass4,\tranceBazz),
				SynthPlayer(\bass5,\tranceBazz2),
				SynthPlayer(\bass6,\tranceBazz3),
				SynthPlayer(\bass7,\tranceBazz2)
			];

			synths[0].seq(\note,0,"0 3 2 7");
			synths[1].seq(\note,0,"10 13 12 19",[\speed,8]);
			synths[2].seq(\note,0," 0 8  13 12  19",[\speed,8]);
			synths[3].seq(\note,0,"  0 8  13 12  19",[\speed,6]);
			synths[4].seq(\note,0,"   0 8  13 12  19",[\speed,2]);
			synths[5].seq(\note,0,"    0 8  13 12  19",[\speed,6]);
			synths[6].seq(\note,0,"     0 8  13 12  19",[\speed,2]);
			synths[7].seq(\note,0,"      0 8  13 12  19",[\speed,4]);
			//
			synths[0].octave=5;
			synths[1].octave=4;
			synths[2].octave=6;
			synths[3].octave=2;
			synths[4].octave=4;
			synths[5].octave=3;
			synths[6].octave=3;
			synths[7].octave=3;


			MIDIClient.sources.collect({|device|
				if( device.device == midiDeviceName, {
					midiDevice = device;
				});
			});

			MIDIIn.connect(0, midiDevice);

			m = i.controllerManager.midi;
			m.addDevice( midiDevice );

			m = ();


			synths.collect({| synth, channelIndex |
				var key = (\channel ++ (channelIndex+1)).asSymbol;
				var channel;
				var fader;
				var knobs = Array.new(3);
				var parameters = [
					[\att,[0,1]],
					[\rel,[0.3,3]],
					[\rq,[0.05,0.1]]
				];

				var ctlIndex;

				channel = i.midi.midi_mix.addControllerGroup(\channel,key);

				channel.addControllerGroup(\cc,\fader);
				channel.addControllerGroup(\cc,\knobs);


				ctlIndex = b[ (channelIndex)*4 + 3 ].ctlNum;
				fader = channel.groups.fader.addController(\fader0,ctlIndex,[0,1]);

				3.do{|j|
					var ctlKey = (\knob++j).asSymbol;
					ctlIndex = b[ (channelIndex)*4 + j ].ctlNum;
					knobs.add( channel.groups.knobs.addController(ctlKey,ctlIndex,[0,1]) );
				};

				i.map( fader, synth, \amp, [0,1] );

				knobs.collect({|ctl,j|
					i.map(ctl,synth,parameters[j][0],parameters[j][1]);
				});


			});


		}}).play;

	});

)
