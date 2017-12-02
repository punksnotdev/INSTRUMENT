// instruments se va a tomar de nodegraph!
//defaultRepetitions

Sequencer : I8Tnode
{

	var <instruments;

	var <>repeat;

	var tdef;

	*new {
		^super.new.init();
	}

	init {

		instruments = Dictionary.new();


		repeat = 4;

	}

	play {

		tdef = Tdef(\sequencer,{

			var beat = 0;

			inf.do{|i|

				instruments.collect({|track|
					track.fwd(i);
				});

				(1/32).wait;
			}


		}).play;

	}

	stop {
		tdef.stop;
	}


	playInstrument {|instrument, position|
		^instruments[instrument.name].play(position);
	}
	stopInstrument {|instrument|
		^instruments[instrument.name].stop();
	}


	registerInstrument {|instrument|
		this.createTrack(instrument);
	}
	unregisterInstrument {|instrument|

	}


	seq {|track,key,pattern,parameters|
		this.addPattern(track,key,pattern,parameters);
	}
	addPattern {|track,key,pattern,parameters|
		this.createTrack(track);
		instruments[ track ].addPattern(key,pattern,parameters);
	}
	removePattern {|track,key|
		instruments[ track ].removePattern(key);
	}


	createTrack {|instrument|

		if( instrument.isKindOf(Instrument), {

			if( instruments[instrument.name] == nil, {
				instruments[instrument.name] = SequencerTrack.new(instrument);
			}, {
				instruments[instrument.name].instrument = instrument;
			});

		},{

			if( instruments[instrument] == nil, {
				instruments[instrument] = SequencerTrack.new();
			});

		});

	}
	deleteTrack {|instrument|

		if( instrument.isKindOf(Instrument), {
			instruments[instrument.name] = nil;
		},{
			instruments[instrument] = nil;
		});

	}


	setInstrumentSpeed{|instrument|

		// instruments[]

	}

}
