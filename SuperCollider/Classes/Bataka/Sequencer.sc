// instruments se va a tomar de nodegraph!
//defaultRepetitions

Sequencer : I8Tnode
{

	var <instruments;

	var <>repeat;

	*new {
		^super.new.init();
	}

	init {

		instruments = Dictionary.new();


		repeat = 4;

	}

	play {

	}


	playInstrument {|instrument, position|
		^instruments[instrument.name].play();
	}
	stopInstrument {|instrument|
		^instruments[instrument.name].stop();
	}


	registerInstrument {|instrument|
		this.createTrack(instrument);
	}
	unregisterInstrument {|instrument|

	}


	seq {|track,key,pattern,repetitions=0|
		this.addPattern(track,key,pattern,repetitions);
	}
	addPattern {|track,key,pattern,repetitions=0|
		this.createTrack(track);
		instruments[ track ].addPattern(key,pattern,repetitions);
	}
	removePattern {|track,key,pattern|
		instruments[ track ].removePattern(key,pattern);
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


}
