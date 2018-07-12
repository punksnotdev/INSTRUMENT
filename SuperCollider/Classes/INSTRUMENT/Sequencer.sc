// instrument_tracks se va a tomar de nodegraph!
//defaultRepetitions

Sequencer : I8Tnode
{

	var <instrument_tracks;

	var <>speed;
	var <>repeat;

	var patterns;
	var sequence;

	var tdef;

	*new {
		// SequencerEvent instances need to have a reference to 'this' (sequencer):

		^super.new.init();
	}

	init {

		SequencerTrack.classSequencer = this;
		SequencerEvent.classSequencer = this;

		instrument_tracks = Dictionary.new();


		speed = 1;
		repeat = 4;

	}

	play {

		tdef = Tdef(\sequencer,{

			var beat = 0;

			inf.do{|i|

				instrument_tracks.collect({|track|
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
		^instrument_tracks[instrument.name].play(position);
	}
	stopInstrument {|instrument|
		^instrument_tracks[instrument.name].stop();
	}


	registerInstrument {|instrument|
		this.createTrack(instrument);
	}
	unregisterInstrument {|instrument|

	}


	seq {|track,parameter,key,pattern,play_parameters|
		"seq in sequencer called".postln;
		this.addPattern(track,parameter,key,pattern,play_parameters);
	}

	addPattern {|track,parameter,key,pattern,play_parameters|
		instrument_tracks[ track ].addPattern(parameter,key,pattern,play_parameters);
	}

	removePattern {|track,parameter,key|
		instrument_tracks[ track ].removePattern(parameter,key);
	}

	getPattern {|track,parameter,key|
		^instrument_tracks[ track ].getPattern(parameter,key);
	}

	setPatternParameters {|track,parameter,key,play_parameters|
		^instrument_tracks[ track ].setPatternParameters(parameter,key,play_parameters);
	}

	createTrack {|instrument|

		if( instrument.isKindOf(Instrument), {

			if( instrument_tracks[instrument.name] == nil, {
				instrument_tracks[instrument.name] = SequencerTrack.new(instrument);
			}, {
				instrument_tracks[instrument.name].instrument = instrument;
			});

		},{

			if( instrument_tracks[instrument] == nil, {
				instrument_tracks[instrument] = SequencerTrack.new();
			});

		});
	}

	deleteTrack {|instrument|

		if( instrument.isKindOf(Instrument), {
			instrument_tracks[instrument.name] = nil;
		},{
			instrument_tracks[instrument] = nil;
		});

	}

	setSpeed{|name_,speed_|
		instrument_tracks[name_].speed = speed_;
	}


}
